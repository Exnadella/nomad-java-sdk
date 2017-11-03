package com.hashicorp.nomad.apimodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hashicorp.nomad.javasdk.ApiObject;
import com.hashicorp.nomad.javasdk.NomadJson;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

/**
 * This is a generated JavaBean representing a request or response structure.
 *
 * @see <a href="https://www.nomadproject.io/docs/http/index.html">Nomad HTTP API</a> documentation associated with the endpoint you are using.
 */
public final class Namespace extends ApiObject {
    private String name;
    private String description;
    private String quota;
    private BigInteger createIndex;
    private BigInteger modifyIndex;

    @JsonProperty("Name")
    public String getName() {
        return name;
    }

    public Namespace setName(String name) {
        this.name = name;
        return this;
    }

    @JsonProperty("Description")
    public String getDescription() {
        return description;
    }

    public Namespace setDescription(String description) {
        this.description = description;
        return this;
    }

    @JsonProperty("Quota")
    public String getQuota() {
        return quota;
    }

    public Namespace setQuota(String quota) {
        this.quota = quota;
        return this;
    }

    @JsonProperty("CreateIndex")
    public BigInteger getCreateIndex() {
        return createIndex;
    }

    public Namespace setCreateIndex(BigInteger createIndex) {
        this.createIndex = createIndex;
        return this;
    }

    @JsonProperty("ModifyIndex")
    public BigInteger getModifyIndex() {
        return modifyIndex;
    }

    public Namespace setModifyIndex(BigInteger modifyIndex) {
        this.modifyIndex = modifyIndex;
        return this;
    }

    @Override
    public String toString() {
        return NomadJson.serialize(this);
    }

    public static Namespace fromJson(String json) throws IOException {
        return NomadJson.deserialize(json, Namespace.class);
    }

    public static List<Namespace> fromJsonArray(String json) throws IOException {
        return NomadJson.deserializeList(json, Namespace.class);
    }
}
