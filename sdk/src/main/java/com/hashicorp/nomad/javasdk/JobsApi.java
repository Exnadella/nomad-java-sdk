package com.hashicorp.nomad.javasdk;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hashicorp.nomad.apimodel.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.RequestBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API for managing and querying jobs,
 * exposing the functionality of the {@code /v1/jobs} and {@code /v1/job} endpoints of the
 * <a href="https://www.nomadproject.io/docs/http/index.html">Nomad HTTP API</a>.
 *
 * @see <a href="https://www.nomadproject.io/docs/http/json-jobs.html">Job Specification</a>
 * for documentation about the {@link Job} structure.
 */
public class JobsApi extends ApiBase {

    JobsApi(final NomadApiClient apiClient) {
        super(apiClient);
    }

    /**
     * Lists the allocations belonging to a job in the active region.
     *
     * @param jobId ID of the job to list allocations for
     * @throws IOException    if there is an HTTP or lower-level problem
     * @throws NomadException if the response signals an error or cannot be deserialized
     * @see <a href="https://www.nomadproject.io/docs/http/job.html">{@code GET /v1/job/<ID>/allocations}</a>
     */
    public ServerQueryResponse<List<AllocationListStub>> allocations(final String jobId)
            throws IOException, NomadException {

        return allocations(jobId, null);
    }

    /**
     * Lists the allocations belonging to a job in the active region.
     *
     * @param jobId   the ID of the job to list allocations for
     * @param options options controlling how the request is performed
     * @throws IOException    if there is an HTTP or lower-level problem
     * @throws NomadException if the response signals an error or cannot be deserialized
     * @see <a href="https://www.nomadproject.io/docs/http/job.html">{@code GET /v1/job/<ID>/allocations}</a>
     */
    public ServerQueryResponse<List<AllocationListStub>> allocations(
            final String jobId,
            @Nullable final QueryOptions<List<AllocationListStub>> options
    ) throws IOException, NomadException {

        return executeServerQuery(
                "/v1/job/" + jobId + "/allocations",
                options,
                NomadJson.parserForListOf(AllocationListStub.class));
    }

    /**
     * Deregisters a job in the active region, and stops all allocations that are part of it.
     *
     * @param jobId ID of the job to deregister
     * @throws IOException    if there is an HTTP or lower-level problem
     * @throws NomadException if the response signals an error or cannot be deserialized
     * @see <a href="https://www.nomadproject.io/docs/http/job.html#delete">{@code DELETE /v1/job/<ID>}</a>
     */
    public EvaluationResponse deregister(final String jobId) throws IOException, NomadException {
        return deregister(jobId, false);
    }

    /**
     * Deregisters a job in the active region, and stops all allocations that are part of it.
     *
     * @param jobId the ID of the job to deregister
     * @param purge If true, the job is deregistered and purged from the system versus still being queryable and
     *              eventually GC'ed from the system. Most callers should not specify purge.
     * @throws IOException    if there is an HTTP or lower-level problem
     * @throws NomadException if the response signals an error or cannot be deserialized
     * @see <a href="https://www.nomadproject.io/docs/http/job.html#delete">{@code DELETE /v1/job/<ID>}</a>
     */
    public EvaluationResponse deregister(final String jobId, final boolean purge)
            throws IOException, NomadException {

        return deregister(jobId, purge, null);
    }

    /**
     * Deregisters a job in the active region,
     * and stops all allocations that are part of it.
     *
     * @param jobId   the ID of the job to deregister
     * @param options options controlling how the request is performed
     * @throws IOException    if there is an HTTP or lower-level problem
     * @throws NomadException if the response signals an error or cannot be deserialized
     * @see <a href="https://www.nomadproject.io/docs/http/job.html#delete">{@code DELETE /v1/job/<ID>}</a>
     */
    public EvaluationResponse deregister(final String jobId, @Nullable final WriteOptions options)
            throws IOException, NomadException {

        return deregister(jobId, false, options);
    }

    /**
     * Deregisters a job in the active region,
     * and stops all allocations that are part of it.
     *
     * @param jobId   the ID of the job to deregister
     * @param purge   If true, the job is deregistered and purged from the system versus still being queryable and
     *                eventually GC'ed from the system. Most callers should not specify purge.
     * @param options options controlling how the request is performed
     * @throws IOException    if there is an HTTP or lower-level problem
     * @throws NomadException if the response signals an error or cannot be deserialized
     * @see <a href="https://www.nomadproject.io/docs/http/job.html#delete">{@code DELETE /v1/job/<ID>}</a>
     */
    public EvaluationResponse deregister(final String jobId, final boolean purge, @Nullable final WriteOptions options)
            throws IOException, NomadException {

        return executeEvaluationCreatingRequest(delete(
                uri("/v1/job/" + jobId).addParameter("purge", Boolean.toString(purge)),
                options
        ));
    }

    /**
     * Dispatches a new instance of a parameterized job in the active region.
     *
     * @param jobId id of the parameterized job to instantiate
     * @throws IOException    if there is an HTTP or lower-level problem
     * @throws NomadException if the response signals an error or cannot be deserialized
     * @see <a href="https://www.nomadproject.io/api/jobs.html#dispatch-job">{@code PUT /v1/job/<ID>/dispatch}</a>
     */
    public ServerResponse<JobDispatchResponse> dispatch(final String jobId) throws IOException, NomadException {
        return dispatch(jobId, null, null);
    }

    /**
     * Dispatches a new instance of a parameterized job in the active region.
     *
     * @param jobId   id of the parameterized job to instantiate
     * @param payload payload for the instantiated job
     * @throws IOException    if there is an HTTP or lower-level problem
     * @throws NomadException if the response signals an error or cannot be deserialized
     * @see <a href="https://www.nomadproject.io/api/jobs.html#dispatch-job">{@code PUT /v1/job/<ID>/dispatch}</a>
     */
    public ServerResponse<JobDispatchResponse> dispatch(
            final String jobId,
            final byte[] payload
    ) throws IOException, NomadException {
        return dispatch(jobId, null, payload);
    }

    /**
     * Dispatches a new instance of a parameterized job in the active region.
     *
     * @param jobId id of the parameterized job to instantiate
     * @param meta  metadata for the instantiated job
     * @throws IOException    if there is an HTTP or lower-level problem
     * @throws NomadException if the response signals an error or cannot be deserialized
     * @see <a href="https://www.nomadproject.io/api/jobs.html#dispatch-job">{@code PUT /v1/job/<ID>/dispatch}</a>
     */
    public ServerResponse<JobDispatchResponse> dispatch(
            final String jobId,
            @Nullable final Map<String, String> meta
    ) throws IOException, NomadException {
        return dispatch(jobId, meta, null);
    }

    /**
     * Dispatches a new instance of a parameterized job in the active region.
     *
     * @param jobId   id of the parameterized job to instantiate
     * @param meta    metadata for the instantiated job
     * @param payload payload for the instantiated job
     * @throws IOException    if there is an HTTP or lower-level problem
     * @throws NomadException if the response signals an error or cannot be deserialized
     * @see <a href="https://www.nomadproject.io/api/jobs.html#dispatch-job">{@code PUT /v1/job/<ID>/dispatch}</a>
     */
    public ServerResponse<JobDispatchResponse> dispatch(
            final String jobId,
            @Nullable final Map<String, String> meta,
            @Nullable final byte[] payload
    ) throws IOException, NomadException {
        return dispatch(jobId, meta, payload, null);
    }

    /**
     * Dispatches a new instance of a parameterized job in the active region.
     *
     * @param jobId   id of the parameterized job to instantiate
     * @param meta    metadata for the instantiated job
     * @param payload payload for the instantiated job
     * @param options options controlling how the request is performed
     * @throws IOException    if there is an HTTP or lower-level problem
     * @throws NomadException if the response signals an error or cannot be deserialized
     * @see <a href="https://www.nomadproject.io/api/jobs.html#dispatch-job">{@code PUT /v1/job/<ID>/dispatch}</a>
     */
    public ServerResponse<JobDispatchResponse> dispatch(
            final String jobId,
            @Nullable final Map<String, String> meta,
            @Nullable final byte[] payload,
            @Nullable WriteOptions options
    ) throws IOException, NomadException {
        return executeServerAction(
                put("/v1/job/" + jobId + "/dispatch", new JobDispatchRequest(jobId, meta, payload), options),
                NomadJson.parserFor(JobDispatchResponse.class));
    }

    /**
     * Lists the evaluations belonging to a job in the active region.
     *
     * @param jobId ID of the job to list evaluations for
     * @throws IOException    if there is an HTTP or lower-level problem
     * @throws NomadException if the response signals an error or cannot be deserialized
     * @see <a href="https://www.nomadproject.io/docs/http/job.html">{@code GET /v1/job/<ID>/evaluations}</a>
     */
    public ServerQueryResponse<List<Evaluation>> evaluations(final String jobId)
            throws IOException, NomadException {

        return evaluations(jobId, null);
    }

    /**
     * Lists the evaluations belonging to a job in the active region.
     *
     * @param jobId   the ID of the job to list evaluations for
     * @param options options controlling how the request is performed
     * @throws IOException    if there is an HTTP or lower-level problem
     * @throws NomadException if the response signals an error or cannot be deserialized
     * @see <a href="https://www.nomadproject.io/docs/http/job.html">{@code GET /v1/job/<ID>/evaluations}</a>
     */
    public ServerQueryResponse<List<Evaluation>> evaluations(
            final String jobId,
            @Nullable final QueryOptions<List<Evaluation>> options
    ) throws IOException, NomadException {

        return executeServerQuery(
                "/v1/job/" + jobId + "/evaluations",
                options,
                NomadJson.parserForSortedListOf(Evaluation.class, EvaluationsApi.NEWEST_TO_OLDEST_EVALUATIONS));
    }

    /**
     * Creates a new evaluation for a job in the active region.
     * <p>
     * This can be used to force run the scheduling logic if necessary.
     *
     * @param jobId ID of the job to evaluate
     * @throws IOException    if there is an HTTP or lower-level problem
     * @throws NomadException if the response signals an error or cannot be deserialized
     * @see <a href="https://www.nomadproject.io/docs/http/job.html">{@code PUT /v1/job/<ID>/evaluate}</a>
     */
    public EvaluationResponse forceEvaluate(final String jobId) throws IOException, NomadException {
        return forceEvaluate(jobId, null);
    }

    /**
     * Creates a new evaluation for a job in the active region.
     * <p>
     * This can be used to force run the scheduling logic if necessary.
     *
     * @param jobId   the ID of the job to evaluate
     * @param options options controlling how the request is performed
     * @throws IOException    if there is an HTTP or lower-level problem
     * @throws NomadException if the response signals an error or cannot be deserialized
     * @see <a href="https://www.nomadproject.io/docs/http/job.html">{@code PUT /v1/job/<ID>/evaluate}</a>
     */
    public EvaluationResponse forceEvaluate(
            final String jobId,
            @Nullable final WriteOptions options) throws IOException, NomadException {

        return executeEvaluationCreatingRequest(put("/v1/job/" + jobId + "/evaluate", options));
    }

    /**
     * Queries a job in the active region.
     *
     * @param jobId ID of the job to query
     * @throws IOException    if there is an HTTP or lower-level problem
     * @throws NomadException if the response signals an error or cannot be deserialized
     * @see <a href="https://www.nomadproject.io/docs/http/job.html">{@code GET /v1/job/{ID}}</a>
     */
    public ServerQueryResponse<Job> info(final String jobId) throws IOException, NomadException {
        return info(jobId, null);
    }

    /**
     * Queries a job in the active region.
     *
     * @param jobId   the ID of the job to query
     * @param options options controlling how the request is performed
     * @throws IOException    if there is an HTTP or lower-level problem
     * @throws NomadException if the response signals an error or cannot be deserialized
     * @see <a href="https://www.nomadproject.io/docs/http/job.html">{@code GET /v1/job/{ID}}</a>
     */
    public ServerQueryResponse<Job> info(
            final String jobId,
            @Nullable final QueryOptions<Job> options
    ) throws IOException, NomadException {
        return executeServerQuery("/v1/job/" + jobId, options, NomadJson.parserFor(Job.class));
    }

    /**
     * Gets the latest deployment belonging to a job.
     *
     * @param jobId the ID of the job
     * @throws IOException    if there is an HTTP or lower-level problem
     * @throws NomadException if the response signals an error or cannot be deserialized
     * @see <a href="https://www.nomadproject.io/api/jobs.html#read-job-39-s-most-recent-deployment">{@code GET /v1/job/<ID>/deployment}</a>
     */
    public ServerQueryResponse<Deployment> latestDeployment(final String jobId)
            throws IOException, NomadException {

        return latestDeployment(jobId, null);
    }

    /**
     * Gets the latest deployment belonging to a job.
     *
     * @param jobId   the ID of the job
     * @param options options controlling how the request is performed
     * @throws IOException    if there is an HTTP or lower-level problem
     * @throws NomadException if the response signals an error or cannot be deserialized
     * @see <a href="https://www.nomadproject.io/api/jobs.html#read-job-39-s-most-recent-deployment">{@code GET /v1/job/<ID>/deployment}</a>
     */
    public ServerQueryResponse<Deployment> latestDeployment(
            final String jobId,
            @Nullable final QueryOptions<Deployment> options
    ) throws IOException, NomadException {

        return executeServerQuery(
                "/v1/job/" + jobId + "/deployment",
                options,
                NomadJson.parserFor(Deployment.class));
    }

    /**
     * Lists jobs in the active region.
     *
     * @throws IOException    if there is an HTTP or lower-level problem
     * @throws NomadException if the response signals an error or cannot be deserialized
     * @see <a href="https://www.nomadproject.io/docs/http/jobs.html">{@code GET /v1/jobs}</a>
     */
    public ServerQueryResponse<List<JobListStub>> list() throws IOException, NomadException {
        return list(null, null);
    }

    /**
     * Lists jobs in the active region.
     *
     * @param options options controlling how the request is performed
     * @throws IOException    if there is an HTTP or lower-level problem
     * @throws NomadException if the response signals an error or cannot be deserialized
     * @see <a href="https://www.nomadproject.io/docs/http/jobs.html">{@code GET /v1/jobs}</a>
     */
    public ServerQueryResponse<List<JobListStub>> list(
            @Nullable final QueryOptions<List<JobListStub>> options
    ) throws IOException, NomadException {

        return list(null, options);
    }

    /**
     * Lists jobs in the active region.
     *
     * @param jobIdPrefix an even-length prefix that, if given,
     *                    restricts the results to only jobs having an ID with this prefix
     * @throws IOException    if there is an HTTP or lower-level problem
     * @throws NomadException if the response signals an error or cannot be deserialized
     * @see <a href="https://www.nomadproject.io/docs/http/jobs.html">{@code GET /v1/jobs}</a>
     */
    public ServerQueryResponse<List<JobListStub>> list(@Nullable final String jobIdPrefix)
            throws IOException, NomadException {

        return list(jobIdPrefix, null);
    }

    /**
     * Lists jobs in the active region.
     *
     * @param jobIdPrefix an even-length prefix that, if given,
     *                    restricts the results to only jobs having an ID with this prefix
     * @param options     options controlling how the request is performed
     * @throws IOException    if there is an HTTP or lower-level problem
     * @throws NomadException if the response signals an error or cannot be deserialized
     * @see <a href="https://www.nomadproject.io/docs/http/jobs.html">{@code GET /v1/jobs}</a>
     */
    public ServerQueryResponse<List<JobListStub>> list(
            @Nullable final String jobIdPrefix,
            @Nullable final QueryOptions<List<JobListStub>> options
    ) throws IOException, NomadException {

        return executeServerQueryForPrefixFilteredList(
                "/v1/jobs",
                jobIdPrefix,
                options,
                NomadJson.parserForListOf(JobListStub.class));
    }

    /**
     * Forces a new instance of a periodic job in the active region.
     * <p>
     * A new instance will be created even if it violates the job's prohibit_overlap settings.
     * As such, this should be only used to immediately run a periodic job.
     *
     * @param jobId ID of the job to force a run of
     * @throws IOException    if there is an HTTP or lower-level problem
     * @throws NomadException if the response signals an error or cannot be deserialized
     * @see <a href="https://www.nomadproject.io/docs/http/job.html">{@code PUT /v1/job/{ID}/periodic/force}</a>
     */
    public EvaluationResponse periodicForce(String jobId) throws IOException, NomadException {
        return periodicForce(jobId, null);
    }

    /**
     * Forces a new instance of a periodic job in the active region.
     * <p>
     * A new instance will be created even if it violates the job's prohibit_overlap settings.
     * As such, this should be only used to immediately run a periodic job.
     *
     * @param jobId   the ID of the job to force a run of
     * @param options options controlling how the request is performed
     * @throws IOException    if there is an HTTP or lower-level problem
     * @throws NomadException if the response signals an error or cannot be deserialized
     * @see <a href="https://www.nomadproject.io/docs/http/job.html">{@code PUT /v1/job/{ID}/periodic/force}</a>
     */
    public EvaluationResponse periodicForce(String jobId, @Nullable WriteOptions options)
            throws IOException, NomadException {
        return executeEvaluationCreatingRequest(
                put("/v1/job/" + jobId + "/periodic/force", options));
    }

    /**
     * Invokes a dry-run of the scheduler for a job in the active region.
     * <p>
     * Can be used together with the modifyIndex parameter of {@link #register(Job, BigInteger) register}
     * to inspect what will happen before registering a job.
     *
     * @param job  detailed specification of the job to plan for
     * @param diff indicates whether a diff between the current and submitted versions of the job
     *             should be included in the response.
     * @throws IOException    if there is an HTTP or lower-level problem
     * @throws NomadException if the response signals an error or cannot be deserialized
     * @see <a href="https://www.nomadproject.io/intro/getting-started/jobs.html#modifying-a-job">Modifying a Job</a>
     * @see <a href="https://www.nomadproject.io/docs/http/job.html">{@code PUT /v1/job/{ID}/periodic/force}</a>
     */
    public ServerResponse<JobPlanResponse> plan(Job job, boolean diff) throws IOException, NomadException {
        return plan(job, diff, null);
    }

    /**
     * Invokes a dry-run of the scheduler for a job in the active region.
     * <p>
     * Can be used together with the modifyIndex parameter of {@link #register(Job, BigInteger) register}
     * to inspect what will happen before registering a job.
     *
     * @param job     detailed specification of the job to plan for
     * @param diff    indicates whether a diff between the current and submitted versions of the job
     *                should be included in the response.
     * @param options options controlling how the request is performed
     * @throws IOException    if there is an HTTP or lower-level problem
     * @throws NomadException if the response signals an error or cannot be deserialized
     * @see <a href="https://www.nomadproject.io/intro/getting-started/jobs.html#modifying-a-job">Modifying a Job</a>
     * @see <a href="https://www.nomadproject.io/docs/http/job.html">{@code PUT /v1/job/{ID}/periodic/force}</a>
     */
    public ServerResponse<JobPlanResponse> plan(
            Job job,
            boolean diff,
            @Nullable WriteOptions options) throws IOException, NomadException {
        return plan(job, diff, false, options);
    }

    /**
     * Invokes a dry-run of the scheduler for a job in the active region.
     * <p>
     * Can be used together with the modifyIndex parameter of {@link #register(Job, BigInteger) register}
     * to inspect what will happen before registering a job.
     *
     * @param job            detailed specification of the job to plan for
     * @param diff           indicates whether a diff between the current and submitted versions of the job
     *                       should be included in the response.
     * @param policyOverride If set, any soft mandatory Sentinel policies will be overriden.
     *                       This allows a job to be registered when it would be denied by policy.
     * @param options        options controlling how the request is performed
     * @throws IOException    if there is an HTTP or lower-level problem
     * @throws NomadException if the response signals an error or cannot be deserialized
     * @see <a href="https://www.nomadproject.io/intro/getting-started/jobs.html#modifying-a-job">Modifying a Job</a>
     * @see <a href="https://www.nomadproject.io/docs/http/job.html">{@code PUT /v1/job/{ID}/periodic/force}</a>
     */
    public ServerResponse<JobPlanResponse> plan(
            Job job,
            boolean diff,
            boolean policyOverride,
            @Nullable WriteOptions options) throws IOException, NomadException {
        return executeServerAction(
                put(uri("/v1/job/" + job.getId() + "/plan"), new JobPlanRequest(job, diff, policyOverride), options),
                NomadJson.parserFor(JobPlanResponse.class));
    }

    /**
     * Registers or updates a job in the active region.
     *
     * @param job detailed specification of the job to register
     * @throws IOException    if there is an HTTP or lower-level problem
     * @throws NomadException if the response signals an error or cannot be deserialized
     * @see <a href="https://www.nomadproject.io/docs/http/jobs.html#put-post">{@code PUT /v1/jobs}</a>
     */
    public EvaluationResponse register(Job job) throws IOException, NomadException {
        return register(job, null, null);
    }

    /**
     * Registers or updates a job in the active region.
     *
     * @param job     detailed specification of the job to register
     * @param options options controlling how the request is performed
     * @throws IOException    if there is an HTTP or lower-level problem
     * @throws NomadException if the response signals an error or cannot be deserialized
     * @see <a href="https://www.nomadproject.io/docs/http/jobs.html#put-post">{@code PUT /v1/jobs}</a>
     */
    public EvaluationResponse register(Job job, @Nullable WriteOptions options) throws IOException, NomadException {
        return register(job, null, options);
    }

    /**
     * Registers or updates a job in the active region.
     *
     * @param job         detailed specification of the job to register
     * @param modifyIndex when specified, the registration is only performed if the job's modify index matches.
     *                    This can be used to make sure the job hasn't changed since getting a
     *                    {@link #plan(Job, boolean) plan}.
     * @throws IOException    if there is an HTTP or lower-level problem
     * @throws NomadException if the response signals an error or cannot be deserialized
     * @see <a href="https://www.nomadproject.io/docs/http/jobs.html#put-post">{@code PUT /v1/jobs}</a>
     */
    public EvaluationResponse register(Job job, @Nullable BigInteger modifyIndex) throws IOException, NomadException {
        return register(job, modifyIndex, null);
    }

    /**
     * Registers or updates a job in the active region.
     *
     * @param job         detailed specification of the job to register
     * @param modifyIndex when specified, the registration is only performed if the job's modify index matches.
     *                    This can be used to make sure the job hasn't changed since getting a
     *                    {@link #plan(Job, boolean) plan}.
     * @param options     options controlling how the request is performed
     * @throws IOException    if there is an HTTP or lower-level problem
     * @throws NomadException if the response signals an error or cannot be deserialized
     * @see <a href="https://www.nomadproject.io/docs/http/jobs.html#put-post">{@code PUT /v1/jobs}</a>
     */
    public EvaluationResponse register(Job job,
                                       @Nullable BigInteger modifyIndex,
                                       @Nullable WriteOptions options) throws IOException, NomadException {
        return register(job, modifyIndex, false, options);
    }

    /**
     * Registers or updates a job in the active region.
     *
     * @param job            detailed specification of the job to register
     * @param modifyIndex    when specified, the registration is only performed if the job's modify index matches.
     *                       This can be used to make sure the job hasn't changed since getting a
     *                       {@link #plan(Job, boolean) plan}.
     * @param policyOverride If true, any soft mandatory Sentinel policies will be overriden.
     *                       This allows a job to be registered when it would be denied by policy.
     * @param options        options controlling how the request is performed
     * @throws IOException    if there is an HTTP or lower-level problem
     * @throws NomadException if the response signals an error or cannot be deserialized
     * @see <a href="https://www.nomadproject.io/docs/http/jobs.html#put-post">{@code PUT /v1/jobs}</a>
     */
    public EvaluationResponse register(Job job,
                                       @Nullable BigInteger modifyIndex,
                                       boolean policyOverride,
                                       @Nullable WriteOptions options) throws IOException, NomadException {
        return executeEvaluationCreatingRequest(
                put("/v1/jobs", new JobRegistrationRequest(job, modifyIndex, policyOverride), options));
    }

    /**
     * Reverts to a prior version of a job.
     *
     * @param jobId        ID of the job
     * @param version      the version to revert to
     * @param priorVersion when set, the job is only reverted if the job's current version matches this prior version
     * @throws IOException    if there is an HTTP or lower-level problem
     * @throws NomadException if the response signals an error or cannot be deserialized
     * @see <a href="https://www.nomadproject.io/api/jobs.html#revert-to-older-job-version">{@code PUT /v1/job/{ID}/revert}</a>
     */
    public EvaluationResponse revert(
            String jobId,
            BigInteger version,
            @Nullable BigInteger priorVersion
    ) throws IOException, NomadException {
        return revert(jobId, version, priorVersion, null);
    }

    /**
     * Reverts to a prior version of a job.
     *
     * @param jobId        ID of the job
     * @param version      the version to revert to
     * @param priorVersion when set, the job is only reverted if the job's current version matches this prior version
     * @param options      options controlling how the request is performed
     * @throws IOException    if there is an HTTP or lower-level problem
     * @throws NomadException if the response signals an error or cannot be deserialized
     * @see <a href="https://www.nomadproject.io/api/jobs.html#revert-to-older-job-version">{@code PUT /v1/job/{ID}/revert}</a>
     */
    public EvaluationResponse revert(
            String jobId,
            BigInteger version,
            @Nullable BigInteger priorVersion,
            @Nullable WriteOptions options
    ) throws IOException, NomadException {
        return executeEvaluationCreatingRequest(
                put("/v1/job/" + jobId + "/revert", new JobRevertRequest(jobId, version, priorVersion), options));
    }

    /**
     * Marks a version of a job as stable or unstable.
     *
     * @param jobId   ID of the job
     * @param version the job version to affect
     * @param stable  whether the job is stable or unstable
     * @throws IOException    if there is an HTTP or lower-level problem
     * @throws NomadException if the response signals an error or cannot be deserialized
     * @see <a href="https://www.nomadproject.io/api/jobs.html#set-job-stability">{@code PUT /v1/job/{ID}/stable}</a>
     */
    public EvaluationResponse stable(
            String jobId,
            BigInteger version,
            boolean stable
    ) throws IOException, NomadException {
        return stable(jobId, version, stable, null);
    }

    /**
     * Marks a version of a job as stable or unstable.
     *
     * @param jobId   ID of the job
     * @param version the job version to affect
     * @param stable  whether the job is stable or unstable
     * @param options options controlling how the request is performed
     * @throws IOException    if there is an HTTP or lower-level problem
     * @throws NomadException if the response signals an error or cannot be deserialized
     * @see <a href="https://www.nomadproject.io/api/jobs.html#set-job-stability">{@code PUT /v1/job/{ID}/stable}</a>
     */
    public EvaluationResponse stable(
            String jobId,
            BigInteger version,
            boolean stable,
            @Nullable WriteOptions options
    ) throws IOException, NomadException {
        return executeEvaluationCreatingRequest(
                put("/v1/job/" + jobId + "/stable", new JobStabilityRequest(jobId, version, stable), options));
    }

    /**
     * Queries the summary of a job in the active region.
     *
     * @param jobId ID of the job to get a summary for
     * @throws IOException    if there is an HTTP or lower-level problem
     * @throws NomadException if the response signals an error or cannot be deserialized
     * @see <a href="https://www.nomadproject.io/docs/http/job.html">{@code GET /v1/job/{ID}/summary}</a>
     */
    public ServerQueryResponse<JobSummary> summary(String jobId) throws IOException, NomadException {
        return summary(jobId, null);
    }

    /**
     * Queries the summary of a job in the active region.
     *
     * @param jobId   ID of the job to get a summary for
     * @param options options controlling how the request is performed
     * @throws IOException    if there is an HTTP or lower-level problem
     * @throws NomadException if the response signals an error or cannot be deserialized
     * @see <a href="https://www.nomadproject.io/docs/http/job.html">{@code GET /v1/job/{ID}/summary}</a>
     */
    public ServerQueryResponse<JobSummary> summary(String jobId, @Nullable QueryOptions<JobSummary> options)
            throws IOException, NomadException {
        return executeServerQuery(
                "/v1/job/" + jobId + "/summary",
                options,
                NomadJson.parserFor(JobSummary.class));
    }

    /**
     * Validates a job.
     *
     * @param job the job to validate
     * @throws IOException    if there is an HTTP or lower-level problem
     * @throws NomadException if the response signals an error or cannot be deserialized
     * @see <a href="https://www.nomadproject.io/api/validate.html#validate-job">{@code PUT /v1/validate/job}</a>
     */
    public ServerResponse<JobValidateResponse> validate(Job job) throws IOException, NomadException {
        return validate(job, null);
    }

    /**
     * Validates a job.
     *
     * @param job     the job to validate
     * @param options options controlling how the request is performed
     * @throws IOException    if there is an HTTP or lower-level problem
     * @throws NomadException if the response signals an error or cannot be deserialized
     * @see <a href="https://www.nomadproject.io/api/validate.html#validate-job">{@code PUT /v1/validate/job}</a>
     */
    public ServerResponse<JobValidateResponse> validate(Job job, @Nullable WriteOptions options)
            throws IOException, NomadException {
        return executeServerAction(
                put("/v1/validate/job", new JobValidationRequest(job), options),
                NomadJson.parserFor(JobValidateResponse.class));
    }

    /**
     * Lists the versions of a job.
     *
     * @param jobId ID of the job
     * @throws IOException    if there is an HTTP or lower-level problem
     * @throws NomadException if the response signals an error or cannot be deserialized
     * @see <a href="https://www.nomadproject.io/api/jobs.html#list-job-versions">{@code GET /v1/job/{ID}/versions}</a>
     */
    public ServerQueryResponse<JobVersionsResponseData> versions(
            String jobId
    ) throws IOException, NomadException {
        return versions(jobId, null);
    }

    /**
     * Lists the versions of a job.
     *
     * @param jobId   ID of the job
     * @param options options controlling how the request is performed
     * @throws IOException    if there is an HTTP or lower-level problem
     * @throws NomadException if the response signals an error or cannot be deserialized
     * @see <a href="https://www.nomadproject.io/api/jobs.html#list-job-versions">{@code GET /v1/job/{ID}/versions}</a>
     */
    public ServerQueryResponse<JobVersionsResponseData> versions(
            String jobId,
            @Nullable QueryOptions<JobVersionsResponseData> options
    ) throws IOException, NomadException {
        return versions(jobId, false, options);
    }

    /**
     * Lists the versions of a job.
     *
     * @param jobId ID of the job
     * @param diffs when true, diffs are returned in addition the the job versions
     * @throws IOException    if there is an HTTP or lower-level problem
     * @throws NomadException if the response signals an error or cannot be deserialized
     * @see <a href="https://www.nomadproject.io/api/jobs.html#list-job-versions">{@code GET /v1/job/{ID}/versions}</a>
     */
    public ServerQueryResponse<JobVersionsResponseData> versions(
            String jobId,
            boolean diffs
    ) throws IOException, NomadException {
        return versions(jobId, diffs, null);
    }

    /**
     * Lists the versions of a job.
     *
     * @param jobId   ID of the job
     * @param diffs   when true, diffs are returned in addition the the job versions
     * @param options options controlling how the request is performed
     * @throws IOException    if there is an HTTP or lower-level problem
     * @throws NomadException if the response signals an error or cannot be deserialized
     * @see <a href="https://www.nomadproject.io/api/jobs.html#list-job-versions">{@code GET /v1/job/{ID}/versions}</a>
     */
    public ServerQueryResponse<JobVersionsResponseData> versions(
            String jobId,
            boolean diffs,
            @Nullable QueryOptions<JobVersionsResponseData> options
    ) throws IOException, NomadException {
        return executeServerQuery(
                uri("/v1/job/" + jobId + "/versions").addParameter("diffs", Boolean.toString(diffs)),
                options,
                NomadJson.parserFor(JobVersionsResponseData.class));
    }

    private EvaluationResponse executeEvaluationCreatingRequest(RequestBuilder request)
            throws IOException, NomadException {
        return apiClient.execute(request, new ResponseAdapter<String, EvaluationResponse>(new ValueExtractor<String>() {
            private final JsonParser<EvalIdResponse> evalIdParser = NomadJson.parserFor(EvalIdResponse.class);

            @Override
            public String extractValue(String json) throws ResponseParsingException {
                return evalIdParser.extractValue(json).evalID;
            }
        }) {
            @Override
            protected EvaluationResponse buildResponse(HttpResponse httpResponse,
                                                       String rawEntity,
                                                       @Nonnull String value) {
                return new EvaluationResponse(httpResponse, rawEntity, value);
            }
        }, null);
    }

    /**
     * Lists the deployments belonging to a job in the active region.
     *
     * @param jobId ID of the job to list deployments for
     * @throws IOException    if there is an HTTP or lower-level problem
     * @throws NomadException if the response signals an error or cannot be deserialized
     * @see <a href="https://www.nomadproject.io/docs/http/job.html">{@code GET /v1/job/<ID>/deployments}</a>
     */
    public ServerQueryResponse<List<Deployment>> deployments(final String jobId)
            throws IOException, NomadException {

        return deployments(jobId, null);
    }

    /**
     * Lists the deployments belonging to a job in the active region.
     *
     * @param jobId   the ID of the job to list deployments for
     * @param options options controlling how the request is performed
     * @throws IOException    if there is an HTTP or lower-level problem
     * @throws NomadException if the response signals an error or cannot be deserialized
     * @see <a href="https://www.nomadproject.io/docs/http/job.html">{@code GET /v1/job/<ID>/deployments}</a>
     */
    public ServerQueryResponse<List<Deployment>> deployments(
            final String jobId,
            @Nullable final QueryOptions<List<Deployment>> options
    ) throws IOException, NomadException {

        return executeServerQuery(
                "/v1/job/" + jobId + "/deployments",
                options,
                NomadJson.parserForListOf(Deployment.class));
    }

    /**
     * Reads scale information about a job.
     *
     * @param jobId   the ID of the job to list deployments for
     * @throws IOException    if there is an HTTP or lower-level problem
     * @throws NomadException if the response signals an error or cannot be deserialized
     * @see <a href="https://www.nomadproject.io/api-docs/jobs/#read-job-scale-status-beta">{@code GET /v1/job/<id>/scale}</a>
     */
    public ServerQueryResponse<JobScaleStatusResponse> scaleStatus(
            final String jobId
    ) throws IOException, NomadException {
        return scaleStatus(jobId, null);
    }

    /**
     * Reads scale information about a job.
     *
     * @param jobId   the ID of the job to list deployments for
     * @param options options controlling how the request is performed
     * @throws IOException    if there is an HTTP or lower-level problem
     * @throws NomadException if the response signals an error or cannot be deserialized
     * @see <a href="https://www.nomadproject.io/api-docs/jobs/#read-job-scale-status-beta">{@code GET /v1/job/<id>/scale}</a>
     */
    public ServerQueryResponse<JobScaleStatusResponse> scaleStatus(
            final String jobId,
            @Nullable final QueryOptions<JobScaleStatusResponse> options
    ) throws IOException, NomadException {

        return executeServerQuery(
                "/v1/job/" + jobId + "/scale",
                options,
                NomadJson.parserFor(JobScaleStatusResponse.class));
    }

    /**
     * Scale task group count.
     *
     * @param jobId   the ID of the job to evaluate
     * @param group   the name of the target group
     * @param count   the new task group count
     * @param message a message describing the scaling event
     * @param meta    metadata to store with the scaling event
     * @param options options controlling how the request is performed
     * @throws IOException    if there is an HTTP or lower-level problem
     * @throws NomadException if the response signals an error or cannot be deserialized
     * @see <a href="https://www.nomadproject.io/docs/http/job.html">{@code PUT /v1/job/<ID>/evaluate}</a>
     */
    public EvaluationResponse scaleGroup(
            final String jobId,
            final String group,
            final Integer count,
            @Nullable final String message,
            @Nullable final Map<String,Object> meta,
            @Nullable final WriteOptions options) throws IOException, NomadException {

        return registerGroupScalingEvent(jobId, group, count, false, message, meta, options);
    }

    /**
     * Register informational scaling event.
     *
     * @param jobId   the ID of the job to evaluate
     * @param group   the name of the target group
     * @param message a message describing the scaling event
     * @param error   desginates an error state
     * @param meta    metadata to store with the scaling event
     * @param options options controlling how the request is performed
     * @throws IOException    if there is an HTTP or lower-level problem
     * @throws NomadException if the response signals an error or cannot be deserialized
     * @see <a href="https://www.nomadproject.io/docs/http/job.html">{@code PUT /v1/job/<ID>/evaluate}</a>
     */
    public EvaluationResponse scaleGroup(
            final String jobId,
            final String group,
            @Nullable final String message,
            @Nullable final Boolean error,
            @Nullable final Map<String,Object> meta,
            @Nullable final WriteOptions options) throws IOException, NomadException {

        return registerGroupScalingEvent(jobId, group, null, error, message, meta, options);
    }

    /** Low-level method for scaling events against a task group.
     * @param jobId the ID of the job
     * @param group the name of the targeted task gropu
     * @param count the count (optional)
     * @param error whether this is an error state or not (defaults false, cannot be true if count == null)
     * @param message description of the scaling event (optional)
     * @param meta metadata for the scaling event (optional)
     * @param options options controlling how the request is performed
     *
     * @throws IOException    if there is an HTTP or lower-level problem
     * @throws NomadException if the response signals an error or cannot be deserialized
     * @see <a href="https://www.nomadproject.io/api-docs/jobs/#scale-task-group-beta">{@code PUT /v1/job/<ID>/scale}</a>
     */
    protected EvaluationResponse registerGroupScalingEvent(
            final String jobId,
            final String group,
            final Integer count,
            @Nullable final Boolean error,
            @Nullable final String message,
            @Nullable final Map<String,Object> meta,
            @Nullable final WriteOptions options) throws IOException, NomadException {

        Map<String,String> target = new HashMap<String,String>();
        target.put("Group", group);
        return executeEvaluationCreatingRequest(put(
                "/v1/job/" + jobId + "/scale",
                new ScalingRequest(count, target, message, error, meta),
                options));
    }

    /**
     * Class matching the JSON request entity for a job scaling event
     */
    private static class ScalingRequest {
        @Nullable Integer Count;
        Map<String,String> Target;
        @Nullable String Message;
        @Nullable Boolean Error;
        @Nullable Map<String,Object> Meta;

        public ScalingRequest(@Nullable Integer count,
                              Map<String, String> target,
                              @Nullable String message,
                              @Nullable Boolean error,
                              @Nullable Map<String, Object> meta) {
            Count = count;
            Target = target;
            Message = message;
            Error = error;
            Meta = meta;
        }
    }

    /**
     * Class matching the JSON request entity for job dispatch requests.
     */
    private static class JobDispatchRequest {
        @JsonProperty("JobID")
        public final String jobId; // Checkstyle suppress VisibilityModifier
        public final Map<String, String> meta; // Checkstyle suppress VisibilityModifier
        public final byte[] payload; // Checkstyle suppress VisibilityModifier

        JobDispatchRequest(String jobId, @Nullable Map<String, String> meta, @Nullable byte[] payload) {
            this.jobId = jobId;
            this.meta = meta;
            this.payload = payload;
        }
    }

    /**
     * Class matching the JSON request entity for job plan requests.
     */
    private static class JobPlanRequest {
        private Job job;
        private final boolean diff;
        private final boolean policyOverride;

        JobPlanRequest(Job job, boolean diff, boolean policyOverride) {
            this.job = job;
            this.diff = diff;
            this.policyOverride = policyOverride;
        }

        public Job getJob() {
            return job;
        }

        public boolean isDiff() {
            return diff;
        }

        public boolean isPolicyOverride() {
            return policyOverride;
        }
    }

    /**
     * Class matching the JSON request entity for job registration requests.
     */
    private static class JobRegistrationRequest {
        public final Job job; // Checkstyle suppress VisibilityModifier
        public final Boolean enforceIndex; // Checkstyle suppress VisibilityModifier
        public final BigInteger jobModifyIndex; // Checkstyle suppress VisibilityModifier
        public final boolean policyOverride; // Checkstyle suppress VisibilityModifier

        JobRegistrationRequest(Job job, @Nullable BigInteger jobModifyIndex, boolean policyOverride) {
            this.job = job;
            this.enforceIndex = jobModifyIndex != null;
            this.jobModifyIndex = jobModifyIndex;
            this.policyOverride = policyOverride;
        }
    }

    /**
     * Class matching the JSON request entity for job revert requests.
     */
    private static class JobRevertRequest {
        @JsonProperty("JobID")
        public final String jobId; // Checkstyle suppress VisibilityModifier
        public final BigInteger jobVersion; // Checkstyle suppress VisibilityModifier
        public final BigInteger enforcePriorVersion; // Checkstyle suppress VisibilityModifier

        JobRevertRequest(String jobId, BigInteger jobVersion, @Nullable BigInteger enforcePriorVersion) {
            this.jobId = jobId;
            this.jobVersion = jobVersion;
            this.enforcePriorVersion = enforcePriorVersion;
        }
    }

    /**
     * Class matching the JSON request entity for job revert requests.
     */
    private static class JobStabilityRequest {
        @JsonProperty("JobID")
        public final String jobId; // Checkstyle suppress VisibilityModifier
        public final BigInteger jobVersion; // Checkstyle suppress VisibilityModifier
        public final boolean stable; // Checkstyle suppress VisibilityModifier

        JobStabilityRequest(String jobId, BigInteger jobVersion, boolean stable) {
            this.jobId = jobId;
            this.jobVersion = jobVersion;
            this.stable = stable;
        }
    }

    /**
     * Class matching the JSON request entity for job validation requests.
     */
    private static class JobValidationRequest {
        public final Job job; // Checkstyle suppress VisibilityModifier

        JobValidationRequest(Job job) {
            this.job = job;
        }
    }

    /**
     * Class matching the JSON that wraps evaluation IDs in responses to evaluation-creating requests.
     */
    private static class EvalIdResponse {
        public String evalID; // Checkstyle suppress VisibilityModifier
    }

}
