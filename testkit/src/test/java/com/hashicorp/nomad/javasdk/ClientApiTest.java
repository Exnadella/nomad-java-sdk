package com.hashicorp.nomad.javasdk;

import com.hashicorp.nomad.apimodel.AllocFileInfo;
import com.hashicorp.nomad.apimodel.AllocResourceUsage;
import com.hashicorp.nomad.apimodel.Allocation;
import com.hashicorp.nomad.apimodel.AllocationListStub;
import com.hashicorp.nomad.apimodel.HostStats;
import com.hashicorp.nomad.apimodel.Job;
import com.hashicorp.nomad.apimodel.Task;
import com.hashicorp.nomad.apimodel.TaskState;
import com.hashicorp.nomad.testutils.TestAgent;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.fail;

public class ClientApiTest extends ApiTestBase {

    @Test
    public void shouldGarbageCollectAGivenAllocation() throws Exception {
        try (TestAgent agent = newClientServer()) {
            final NomadApiClient apiClient = agent.getApiClient();

            final Job job = createTestJob();
            job.getTaskGroups().get(0).getTasks().get(0).addConfig("run_for", "0s");
            final String evalId = registerTestJobAndPollUntilEvaluationCompletesSuccessfully(agent, job).getId();

            final AllocationListStub allocation = apiClient.getEvaluationsApi().allocations(evalId).getValue().get(0);
            final ClientApi clientApi = apiClient.lookupClientApiByNodeId(allocation.getNodeId());

            NomadResponse<Void> gcResponse = clientApi.garbageCollect(allocation.getId());

            int numTries = 0;
            while (true) {
                assertThat("timed out waiting for evidence of gc", numTries, lessThan(5));
                try {
                    clientApi.ls(allocation.getId(), "/");
                } catch (ErrorResponseException e) {
                    assertThat(e.getServerErrorCode(), is(500));
                    break;
                }
                Thread.sleep(1000l);
                numTries++;
            }
        }
    }

    @Test
    public void shouldGetClientStatistics() throws Exception {
        try (TestAgent agent = newClientServer()) {
            ClientApi clientApi = agent.getApiClient().getClientApi(agent.getHttpAddress());

            NomadResponse<HostStats> response = clientApi.stats();
            HostStats stats = response.getValue();
            assertThat(stats.getUptime(), greaterThan(BigInteger.ZERO));
            assertThat(stats.getDeviceStats(), anyOf(empty(), nullValue()));
        }
    }

    @Test
    public void shouldGetAllocationStatistics() throws Exception {
        try (TestAgent agent = newClientServer()) {
            AllocationsApi allocationsApi = agent.getApiClient().getAllocationsApi();

            registerTestJobAndPollUntilEvaluationCompletesSuccessfully(agent);

            ServerQueryResponse<List<AllocationListStub>> listResponse = allocationsApi.list();
            assertThat(listResponse.getValue(), not(empty()));
            AllocationListStub allocation = listResponse.getValue().get(0);

            ClientApi clientApi = agent.getApiClient().lookupClientApiByNodeId(allocation.getNodeId());

            NomadResponse<AllocResourceUsage> statsResponse = clientApi.stats(allocation.getId());
            assertThat(statsResponse.getValue().getResourceUsage(), not(nullValue()));
        }
    }

    private Allocation runAndPollForTaskState(TestAgent agent, String bashCommand, Matcher<? super TaskState> matcher) throws Exception {
        Job job = createBashJob(bashCommand);

        registerTestJobAndPollUntilEvaluationCompletesSuccessfully(agent, job);

        AllocationsApi allocationsApi = agent.getApiClient().getAllocationsApi();

        ServerQueryResponse<List<AllocationListStub>> listResponse = allocationsApi.list();
        assertThat(listResponse.getValue(), not(empty()));
        AllocationListStub allocation = listResponse.getValue().get(0);

        return pollForTaskState(allocationsApi, allocation.getId(), "task1", matcher);
    }

    private Allocation pollForTaskState(AllocationsApi allocationsApi, String allocationId, String taskName, Matcher<? super TaskState> matcher) throws Exception {
        ServerQueryResponse<Allocation> infoResponse = allocationsApi.info(allocationId);
        try {
            while (infoResponse.getValue().getTaskStates() == null || !matcher.matches(infoResponse.getValue().getTaskStates().get(taskName))) {
                infoResponse = allocationsApi.info(allocationId, QueryOptions.newerThan(infoResponse));
            }
        } catch (Throwable e) {
            Description description = new StringDescription();
            final Map<String, TaskState> taskStates = infoResponse.getValue().getTaskStates();
            matcher.describeMismatch(taskStates == null ? null : taskStates.get(taskName), description);
            throw new RuntimeException("Last mismatch was: " + description, e);
        }
        return infoResponse.getValue();
    }

    @Test
    public void shouldCatAFileCreatedByATask() throws Exception {
        try (TestAgent agent = newClientServer()) {
            Allocation allocation = runAndPollForTaskState(agent, "echo hi > $NOMAD_ALLOC_DIR/out.txt", notNullValue());

            ClientApi clientApi = agent.getApiClient().lookupClientApiByNodeId(allocation.getNodeId());

            NomadResponse<String> statsResponse = clientApi.cat(allocation.getId(), "alloc/out.txt");
            assertThat(statsResponse.getValue(), is("hi\n"));
        }
    }

    @Test
    public void shouldReadPartOfAFileCreatedByATask() throws Exception {
        try (TestAgent agent = newClientServer()) {
            Allocation allocation = runAndPollForTaskState(agent, "echo abcdefghijklmnopqrstuvwxyz > $NOMAD_ALLOC_DIR/out.txt", notNullValue());

            ClientApi clientApi = agent.getApiClient().lookupClientApiByNodeId(allocation.getNodeId());

            NomadResponse<String> statsResponse = clientApi.readAt(allocation.getId(), "/alloc/out.txt", 5, 13);
            assertThat(statsResponse.getValue(), is("fghijklmnopqr"));
        }
    }

    @Test
    public void shouldStreamAFileCreatedByATask() throws Exception {
        try (TestAgent agent = newClientServer()) {
            Allocation allocation = runAndPollForTaskState(agent, "echo abcdefghijklmnopqrstuvwxyz > $NOMAD_ALLOC_DIR/out.txt", notNullValue());

            ClientApi clientApi = agent.getApiClient().lookupClientApiByNodeId(allocation.getNodeId());

            FramedStream allStream = clientApi.stream(allocation.getId(), "alloc/out.txt");
            assertStreamResultIs("abcdefghijklmnopqrstuvwxyz\n", allStream);

            FramedStream offsetStream = clientApi.stream(allocation.getId(), "alloc/out.txt", 5L);
            assertStreamResultIs("fghijklmnopqrstuvwxyz\n", offsetStream);

            FramedStream offsetFromEndStream = clientApi.stream(allocation.getId(), "alloc/out.txt", 5L, "end");
            assertStreamResultIs("wxyz\n", offsetFromEndStream);
        }
    }


    @Test
    public void shouldStreamFramesFromAFileCreatedByATask() throws Exception {
        try (TestAgent agent = newClientServer()) {
            Allocation allocation = runAndPollForTaskState(agent, "echo abcdefghijklmnopqrstuvwxyz > $NOMAD_ALLOC_DIR/out.txt", notNullValue());

            ClientApi clientApi = agent.getApiClient().lookupClientApiByNodeId(allocation.getNodeId());

            FramedStream allStream = clientApi.stream(allocation.getId(), "alloc/out.txt");
            assertStreamResultIs("abcdefghijklmnopqrstuvwxyz\n", allStream);

            FramedStream offsetStream = clientApi.stream(allocation.getId(), "alloc/out.txt", 5L);
            assertStreamResultIs("fghijklmnopqrstuvwxyz\n", offsetStream);

            FramedStream offsetFromEndStream = clientApi.stream(allocation.getId(), "alloc/out.txt", 5L, "end");
            assertStreamResultIs("wxyz\n", offsetFromEndStream);
        }
    }

    private void assertStreamResultIs(final String expectedResult, final InputStream stream) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            final int expectedBytes = expectedResult.getBytes().length;
            final byte[] buffer = new byte[(int)(0.75 * expectedBytes)];
            do {
                final int len = stream.read(buffer);
                if (len < 0)
                    fail("Expected " + expectedBytes + " but got " + out.size() + ": " + out);
                out.write(buffer, 0, len);
            } while (out.size() < expectedResult.length());
        } finally {
            stream.close();
        }
        assertThat(out.toString("UTF-8"), is(expectedResult));
    }

    private void assertStreamResultIs(final String expectedResult, final FramedStream stream) throws IOException {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            while (stream.hasNextFrame()) {
                final byte[] data = stream.nextFrame().getData();
                if (data != null)
                    out.write(data);
                else if (out.size() >= expectedResult.length())
                    break;
            }
        } finally {
            stream.close();
        }
        assertThat(out.toString("UTF-8"), is(expectedResult));
    }

    @Test
    public void shouldStreamLogsFromAnAllocation() throws Exception {
        try (TestAgent agent = newClientServer()) {
            final Allocation allocation = runAndPollForTaskState(agent, "echo hi", hasProperty("state", is("dead")));

            ClientApi clientApi = agent.getApiClient().lookupClientApiByNodeId(allocation.getNodeId());

            InputStream stream = clientApi.logs(allocation.getId(), "task1", false, "stdout");
            assertStreamResultIs("hi\n", stream);
        }
    }

    @Test
    public void shouldStreamLogsAsFramesFromAnAllocation() throws Exception {
        try (TestAgent agent = newClientServer()) {
            final Allocation allocation = runAndPollForTaskState(agent, "echo hi", hasProperty("state", is("dead")));

            ClientApi clientApi = agent.getApiClient().lookupClientApiByNodeId(allocation.getNodeId());

            FramedStream stream = clientApi.logsAsFrames(allocation.getId(), "task1", false, "stdout");
            assertStreamResultIs("hi\n", stream);
        }
    }

    @Test
    public void shouldListFilesCreatedByTask() throws Exception {
        try (TestAgent agent = newClientServer()) {
            Allocation allocation = runAndPollForTaskState(agent, "echo hi > $NOMAD_ALLOC_DIR/a.txt; echo bye > $NOMAD_ALLOC_DIR/b.txt", notNullValue());

            ClientApi clientApi = agent.getApiClient().lookupClientApiByNodeId(allocation.getNodeId());

            NomadResponse<List<AllocFileInfo>> listResponse = clientApi.ls(allocation.getId(), "/");
            assertThat(listResponse.getValue(), hasSize(2));
            AllocFileInfo fileInfo = listResponse.getValue().get(0);
            assertThat(fileInfo.getName(), is("alloc"));
            assertThat(fileInfo.getIsDir(), is(true));
        }
    }

    @Test
    public void shouldStatFileCreatedByTask() throws Exception {
        try (TestAgent agent = newClientServer()) {
            Allocation allocation = runAndPollForTaskState(agent, "echo hi > $NOMAD_ALLOC_DIR/a.txt", notNullValue());

            ClientApi clientApi = agent.getApiClient().lookupClientApiByNodeId(allocation.getNodeId());

            NomadResponse<AllocFileInfo> statResponse = clientApi.stat(allocation.getId(), "/alloc/a.txt");
            AllocFileInfo fileInfo = statResponse.getValue();
            assertThat(fileInfo.getName(), is("a.txt"));
            assertThat(fileInfo.getIsDir(), is(false));
            assertThat(fileInfo.getContentType(), is("application/octet-stream"));
        }
    }

}
