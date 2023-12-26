package org.spoken_tutorial.health.elasticsearch.models;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class QueueManagement {

    @Id
    @Column(name = "queueId", nullable = false, updatable = false)
    private long queueId;

    @Column(name = "requestId", nullable = true)
    private String requestId;

    @Column(name = "requestTime", nullable = true, updatable = false)
    private Timestamp requestTime;

    @Column(name = "requestType", nullable = true)
    private String requestType;

    @Column(name = "status", nullable = true)
    private String status;

    @Column(name = "startTime", nullable = true)
    private long startTime;

    @Column(name = "endTime", nullable = true)
    private long endTime;

    @Column(name = "procesingTime", nullable = true)
    private long procesingTime;

    @Column(name = "message", nullable = true)
    private String message;

    @Column(name = "documentId", nullable = true)
    private String documentId;

    @Column(name = "documentType", nullable = true)
    private String documentType;

    @Column(name = "documentPath", nullable = true)
    private String documentPath;

    @Column(name = "documentUrl", nullable = true)
    private String documentUrl;

    @Column(name = "rankView", nullable = true)
    private int rank;

    @Column(name = "view_url", nullable = true)
    private String view_url;

    @Column(name = "language", nullable = true)
    private String language;

    @Column(name = "category", nullable = true)
    private String category;

    @Column(name = "topic", nullable = true)
    private String topic;

    @Column(name = "outlinePath", nullable = true)
    private String outlinePth;

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getDocumentPath() {
        return documentPath;
    }

    public void setDocumentPath(String documentPath) {
        this.documentPath = documentPath;
    }

    public String getDocumentUrl() {
        return documentUrl;
    }

    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }

    public String getOutlinePth() {
        return outlinePth;
    }

    public void setOutlinePth(String outlinePth) {
        this.outlinePth = outlinePth;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getView_url() {
        return view_url;
    }

    public void setView_url(String view_url) {
        this.view_url = view_url;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public long getQueueId() {
        return queueId;
    }

    public void setQueueId(long queueId) {
        this.queueId = queueId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Timestamp getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(Timestamp requestTime) {
        this.requestTime = requestTime;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getProcesingTime() {
        return procesingTime;
    }

    public void setProcesingTime(long procesingTime) {
        this.procesingTime = procesingTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public QueueManagement() {
        super();

    }

    public QueueManagement(long queueId, Timestamp request, String requestType, String status, long startTime,
            long endTime, long procesingTime, String message) {
        super();
        this.queueId = queueId;
        this.requestTime = request;
        this.requestType = requestType;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
        this.procesingTime = procesingTime;
        this.message = message;
    }

    public QueueManagement(long queueId, String requestId, Timestamp requestTime, String requestType) {
        super();
        this.queueId = queueId;
        this.requestId = requestId;
        this.requestTime = requestTime;
        this.requestType = requestType;
    }

}
