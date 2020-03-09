package com.hashicorp.nomad.apimodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hashicorp.nomad.javasdk.ApiObject;
import com.hashicorp.nomad.javasdk.NomadJson;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * This is a generated JavaBean representing a request or response structure.
 *
 * @see <a href="https://www.nomadproject.io/docs/http/index.html">Nomad HTTP API</a> documentation associated with the endpoint you are using.
 */
public final class ServiceCheck extends ApiObject {
    private String id;
    private String name;
    private String type;
    private String command;
    private List<String> args;
    private String path;
    private String protocol;
    private String portLabel;
    private String addressMode;
    private long interval;
    private long timeout;
    private String initialStatus;
    private boolean tlsSkipVerify;
    private Map<String, List<String>> header;
    private String method;
    private CheckRestart checkRestart;
    private String grpcService;
    private boolean grpcUseTls;
    private String taskName;

    @JsonProperty("Id")
    public String getId() {
        return id;
    }

    public ServiceCheck setId(String id) {
        this.id = id;
        return this;
    }

    @JsonProperty("Name")
    public String getName() {
        return name;
    }

    public ServiceCheck setName(String name) {
        this.name = name;
        return this;
    }

    @JsonProperty("Type")
    public String getType() {
        return type;
    }

    public ServiceCheck setType(String type) {
        this.type = type;
        return this;
    }

    @JsonProperty("Command")
    public String getCommand() {
        return command;
    }

    public ServiceCheck setCommand(String command) {
        this.command = command;
        return this;
    }

    @JsonProperty("Args")
    public List<String> getArgs() {
        return args;
    }

    public ServiceCheck setArgs(List<String> args) {
        this.args = args;
        return this;
    }

    public ServiceCheck addArgs(String... args) {
        if (this.args == null)
            this.args = new java.util.ArrayList<>();
        for (String item : args)
            this.args.add(item);
        return this;
    }

    @JsonProperty("Path")
    public String getPath() {
        return path;
    }

    public ServiceCheck setPath(String path) {
        this.path = path;
        return this;
    }

    @JsonProperty("Protocol")
    public String getProtocol() {
        return protocol;
    }

    public ServiceCheck setProtocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    @JsonProperty("PortLabel")
    public String getPortLabel() {
        return portLabel;
    }

    public ServiceCheck setPortLabel(String portLabel) {
        this.portLabel = portLabel;
        return this;
    }

    @JsonProperty("AddressMode")
    public String getAddressMode() {
        return addressMode;
    }

    public ServiceCheck setAddressMode(String addressMode) {
        this.addressMode = addressMode;
        return this;
    }

    @JsonProperty("Interval")
    public long getInterval() {
        return interval;
    }

    public ServiceCheck setInterval(long interval) {
        this.interval = interval;
        return this;
    }

    @JsonProperty("Timeout")
    public long getTimeout() {
        return timeout;
    }

    public ServiceCheck setTimeout(long timeout) {
        this.timeout = timeout;
        return this;
    }

    @JsonProperty("InitialStatus")
    public String getInitialStatus() {
        return initialStatus;
    }

    public ServiceCheck setInitialStatus(String initialStatus) {
        this.initialStatus = initialStatus;
        return this;
    }

    @JsonProperty("TLSSkipVerify")
    public boolean getTlsSkipVerify() {
        return tlsSkipVerify;
    }

    public ServiceCheck setTlsSkipVerify(boolean tlsSkipVerify) {
        this.tlsSkipVerify = tlsSkipVerify;
        return this;
    }

    @JsonProperty("Header")
    public Map<String, List<String>> getHeader() {
        return header;
    }

    public ServiceCheck setHeader(Map<String, List<String>> header) {
        this.header = header;
        return this;
    }

    public ServiceCheck addHeader(String key, List<String> value) {
        if (this.header == null)
            this.header = new java.util.HashMap<>();
        this.header.put(key, value);
        return this;
    }

    @JsonProperty("Method")
    public String getMethod() {
        return method;
    }

    public ServiceCheck setMethod(String method) {
        this.method = method;
        return this;
    }

    @JsonProperty("CheckRestart")
    public CheckRestart getCheckRestart() {
        return checkRestart;
    }

    public ServiceCheck setCheckRestart(CheckRestart checkRestart) {
        this.checkRestart = checkRestart;
        return this;
    }

    @JsonProperty("GRPCService")
    public String getGrpcService() {
        return grpcService;
    }

    public ServiceCheck setGrpcService(String grpcService) {
        this.grpcService = grpcService;
        return this;
    }

    @JsonProperty("GRPCUseTLS")
    public boolean getGrpcUseTls() {
        return grpcUseTls;
    }

    public ServiceCheck setGrpcUseTls(boolean grpcUseTls) {
        this.grpcUseTls = grpcUseTls;
        return this;
    }

    @JsonProperty("TaskName")
    public String getTaskName() {
        return taskName;
    }

    public ServiceCheck setTaskName(String taskName) {
        this.taskName = taskName;
        return this;
    }

    @Override
    public String toString() {
        return NomadJson.serialize(this);
    }

    public static ServiceCheck fromJson(String json) throws IOException {
        return NomadJson.deserialize(json, ServiceCheck.class);
    }

    public static List<ServiceCheck> fromJsonArray(String json) throws IOException {
        return NomadJson.deserializeList(json, ServiceCheck.class);
    }
}
