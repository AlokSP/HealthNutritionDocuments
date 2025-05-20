package org.spoken_tutorial.health.elasticsearch.controllers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spoken_tutorial.health.elasticsearch.JsonService.JsonService;
import org.spoken_tutorial.health.elasticsearch.config.Config;
import org.spoken_tutorial.health.elasticsearch.models.DocumentSearch;
import org.spoken_tutorial.health.elasticsearch.models.QueueManagement;
import org.spoken_tutorial.health.elasticsearch.repositories.QueueManagementRepository;
import org.spoken_tutorial.health.elasticsearch.services.DocumentSearchService;
import org.spoken_tutorial.health.elasticsearch.services.QueueManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private QueueManagementRepository queRepo;

    @Autowired
    private QueueManagementService queuemntService;

    @Autowired
    private Config config;

    @Autowired
    private ElasticsearchOperations operations;

    @Autowired
    private DocumentSearchService docuSearchService;

    @Autowired
    private JsonService jsonService;

    private Timestamp getCurrentTime() {

        Date date = new Date();
        long t = date.getTime();
        Timestamp st = new Timestamp(t);

        return st;
    }

    private boolean doesFileExist(String filePath) {
        if (filePath.startsWith("https://")) {
            return true;
        }
        if (filePath.contains("..") || !filePath.startsWith(config.BASE_NAME)) {
            return false;
        }
        Path path = Paths.get(config.BASE_PATH, filePath);

        return Files.exists(path);
    }

    @GetMapping("/queueStatus/{queueId}")
    public Map<String, String> queueStatus(@PathVariable Long queueId) {

        Map<String, String> resultMap = new HashMap<>();
        QueueManagement queuemnt = queRepo.findByQueueId(queueId);

        if (queuemnt == null) {
            resultMap.put(Config.STATUS, Config.STATUS_NOTFOUND);

        } else {

            String status = queuemnt.getStatus();

            if (status.equals(Config.STATUS_PENDING)) {
                resultMap.put(Config.STATUS, status);
            }

            else if (status.equals(Config.STATUS_PROCESSING)) {
                resultMap.put(Config.STATUS, status);
                resultMap.put(Config.START_TIME, Long.toString(queuemnt.getStartTime()));
                resultMap.put(Config.CURRENT_TIME, Long.toString(System.currentTimeMillis()));
            } else if (status.equals(Config.STATUS_QUEUED)) {
                resultMap.put(Config.STATUS, status);
                resultMap.put(Config.QUEUE_TIME, Long.toString(queuemnt.getQueueTime()));
                resultMap.put(Config.CURRENT_TIME, Long.toString(System.currentTimeMillis()));
            } else if (status.equals(Config.STATUS_DONE)) {
                resultMap.put(Config.STATUS, status);
                resultMap.put(Config.START_TIME, Long.toString(queuemnt.getStartTime()));
                resultMap.put(Config.END_TIME, Long.toString(queuemnt.getEndTime()));
                resultMap.put(Config.PROCESSING_TIME, Long.toString(queuemnt.getProcesingTime()));

            }

            else if (status.equals(Config.STATUS_FAILED)) {
                resultMap.put(Config.STATUS, status);
                resultMap.put(Config.REASON, queuemnt.getReason());
                resultMap.put(Config.START_TIME, Long.toString(queuemnt.getStartTime()));
                resultMap.put(Config.END_TIME, Long.toString(queuemnt.getEndTime()));
                resultMap.put(Config.PROCESSING_TIME, Long.toString(queuemnt.getProcesingTime()));
            }

        }

        return resultMap;

    }

    @GetMapping("/documentStatus/{documentId}")
    public Map<String, String> documentStatus(@PathVariable String documentId) {

        Map<String, String> resultMap = new HashMap<>();
        DocumentSearch documentSearch = docuSearchService.findByDocumentId(documentId);

        if (documentSearch == null) {
            resultMap.put(Config.STATUS, Config.STATUS_NOTFOUND);
        } else {
            resultMap.put(Config.DOCUMENT_ID, documentSearch.getDocumentId());
            resultMap.put(Config.DOCUMENT_TYPE, documentSearch.getDocumentType());
            resultMap.put(Config.LANGUAGE, documentSearch.getLanguage());
            resultMap.put(Config.RANK, Integer.toString(documentSearch.getRank()));
            resultMap.put(Config.CREATION_TIME, Long.toString(documentSearch.getCreationTime()));
            resultMap.put(Config.MODIFICATION_TIME, Long.toString(documentSearch.getModificationTime()));
            resultMap.put(Config.CHANGE_TIME, Long.toString(documentSearch.getChangeTime()));
        }
        return resultMap;

    }

    @GetMapping("/")
    public String Wlecome() {
        return "Welcome to Health and Nutrition Elastic Search Application";
    }

    @GetMapping("/findAll")
    public List<QueueManagement> findAll() {
        return queRepo.findAll();
    }

    public Map<String, String> addDocument(String documentId, String documentType, String documentPath,
            String documentUrl, int rank, String view_url, int languageId, String language,
            Optional<Integer> categoryId, Optional<String> category, Optional<Integer> topicId, Optional<String> topic,
            Optional<String> outlinePath, String requestType, Optional<String> videoPath, Optional<String> title,
            Optional<String> description, Optional<String> thumbnailPath, Optional<Integer> orderValue) {

        Map<String, String> resultMap = new HashMap<>();

        logger.info(
                "RequestType:{} Language:{} View_URL: {} documentId:{} documentPath:{} documentType:{} outlinePath:{}",
                requestType, language, view_url, documentId, documentPath, documentType, outlinePath);

        if (documentPath != null && !doesFileExist(documentPath)) {
            resultMap.put(Config.STATUS, Config.STATUS_FAILED);
            resultMap.put(Config.REASON, "document file does not exist");
            return resultMap;
        }

        if (outlinePath != null && outlinePath.isPresent() && !doesFileExist(outlinePath.get())) {

            resultMap.put(Config.STATUS, Config.STATUS_FAILED);
            resultMap.put(Config.REASON, "outline file does not exist");
            return resultMap;

        }

        QueueManagement queuemnt = new QueueManagement();

        if (outlinePath != null && outlinePath.isPresent())
            queuemnt.setOutlinePath(outlinePath.get());
        queuemnt.setRequestTime(getCurrentTime());
        queuemnt.setRequestType(requestType);
        queuemnt.setDocumentId(documentId);
        queuemnt.setDocumentType(documentType);
        queuemnt.setDocumentPath(documentPath);
        queuemnt.setDocumentUrl(documentUrl);
        queuemnt.setRank(rank);
        queuemnt.setViewUrl(view_url);
        queuemnt.setLanguageId(languageId);
        queuemnt.setStatus("pending");
        if (language != null)
            queuemnt.setLanguage(language);
        if (category != null && category.isPresent())
            queuemnt.setCategory(category.get());
        if (categoryId != null && categoryId.isPresent())
            queuemnt.setCategoryId(categoryId.get());
        if (topic != null && topic.isPresent())
            queuemnt.setTopic(topic.get());
        if (topicId != null && topicId.isPresent())
            queuemnt.setTopicId(topicId.get());

        if (videoPath != null && videoPath.isPresent())
            queuemnt.setVideoPath(videoPath.get());

        if (title != null && title.isPresent())
            queuemnt.setTitle(title.get());

        if (description != null && description.isPresent())
            queuemnt.setDescription(description.get());

        if (thumbnailPath != null && thumbnailPath.isPresent())
            queuemnt.setThumbnailPath(thumbnailPath.get());

        if (orderValue != null && orderValue.isPresent())
            queuemnt.setOrderValue(orderValue.get());

        queRepo.save(queuemnt);

        resultMap.put(Config.QUEUE_ID, Long.toString(queuemnt.getQueueId()));
        resultMap.put(Config.STATUS, Config.SUCCESS);

        return resultMap;

    }

    @PostMapping("/addDocument/{documentId}/{documentType}/{languageId}/{language}/{rank}")
    public Map<String, String> addDocument(@PathVariable String documentId, @PathVariable String documentType,
            @PathVariable int languageId, @PathVariable String language, @PathVariable int rank,
            @RequestParam String documentPath, @RequestParam String documentUrl, @RequestParam String view_url,
            @RequestParam Optional<Integer> categoryId, @RequestParam Optional<String> category,
            @RequestParam Optional<Integer> topicId, @RequestParam Optional<String> topic,
            @RequestParam Optional<String> outlinePath, @RequestParam Optional<String> videoPath,
            @RequestParam Optional<String> title, @RequestParam Optional<String> description,
            @RequestParam Optional<String> thumbnailPath, Optional<Integer> orderValue) {

        return addDocument(documentId, documentType, documentPath, documentUrl, rank, view_url, languageId, language,
                categoryId, category, topicId, topic, outlinePath, Config.ADD_DOCUMENT, videoPath, title, description,
                thumbnailPath, orderValue);
    }

    @PostMapping("/updateDocument/{documentId}/{documentType}/{languageId}/{language}/{rank}")
    public Map<String, String> updateDocument(@PathVariable String documentId, @PathVariable String documentType,
            @PathVariable int languageId, @PathVariable String language, @PathVariable int rank,
            @RequestParam String documentPath, @RequestParam String documentUrl, @RequestParam String view_url,
            @RequestParam Optional<String> category, @RequestParam Optional<Integer> categoryId,
            @RequestParam Optional<String> topic, @RequestParam Optional<Integer> topicId,
            @RequestParam Optional<String> outlinePath, Optional<String> videoPath,
            @RequestParam Optional<String> title, @RequestParam Optional<String> description,
            @RequestParam Optional<String> thumbnailPath, Optional<Integer> orderValue) {

        return addDocument(documentId, documentType, documentPath, documentUrl, rank, view_url, languageId, language,
                categoryId, category, topicId, topic, outlinePath, Config.UPDATE_DOCUMENT, videoPath, title,
                description, thumbnailPath, orderValue);
    }

    @GetMapping("/updateDocumentRank/{documentId}/{documentType}/{languageId}/{rank}")
    public Map<String, String> updateDocumentRank(@PathVariable String documentId, @PathVariable String documentType,
            @PathVariable int languageId, @PathVariable int rank) {
        return addDocument(documentId, documentType, null, null, rank, null, languageId, null, null, null, null, null,
                null, Config.UPDATE_DOCUMENT_RANK, null, null, null, null, null);
    }

    @GetMapping("/deleteDocument/{documentId}/{documentType}/{languageId}")
    public Map<String, String> deleteDocument(@PathVariable String documentId, @PathVariable String documentType,
            @PathVariable int languageId) {

        return addDocument(documentId, documentType, null, null, 0, null, languageId, null, null, null, null, null,
                null, Config.DELETE_DOCUMENT, null, null, null, null, null);
    }

    @PostMapping("/search")
    public Map<String, List<DocumentSearch>> findByDocumentContent(@RequestParam Optional<Integer> categoryId,
            @RequestParam Optional<Integer> topicId, @RequestParam Optional<Integer> languageId,
            @RequestParam Optional<String> query, @RequestParam Optional<String> typeTutorial,
            @RequestParam Optional<String> typeTimeScript, @RequestParam Optional<String> typeBrochure,
            @RequestParam Optional<String> typeResearchPaper) {

        Map<String, List<DocumentSearch>> documentSearchMap = new HashMap<>();
        Criteria criteria = new Criteria();

        logger.info("categoryId: {} , topicId: {} , languageId : {}, query: {}  ", categoryId.orElse(null),
                topicId.orElse(null), languageId.orElse(null), query.orElse(null));

        if (categoryId.isPresent() && categoryId.get() != 0) {
            criteria = criteria.and("categoryId").is(categoryId.get());
        }

        if (topicId.isPresent() && topicId.get() != 0) {
            criteria = criteria.and("topicId").is(topicId.get());
        }

        if (languageId.isPresent() && languageId.get() != 0) {
            criteria = criteria.and("languageId").is(languageId.get());
        }

        if (query.isPresent() && !query.get().isEmpty()) {
            Criteria subCriteria1 = new Criteria().or("documentContent").is(query.get()).or("outlineIndex")
                    .is(query.get());
            criteria = criteria.subCriteria(subCriteria1);

        }

        Criteria subCriteria2 = null;

        if (typeTutorial.isPresent() && !typeTutorial.get().isEmpty()) {

            subCriteria2 = new Criteria("documentType").is(typeTutorial.get());

        }

        if (typeTimeScript.isPresent() && !typeTimeScript.get().isEmpty()) {

            if (subCriteria2 != null) {
                subCriteria2 = subCriteria2.or("documentType").is(typeTimeScript.get());
            } else {
                subCriteria2 = new Criteria("documentType").is(typeTimeScript.get());
            }

        }

        if (typeBrochure.isPresent() && !typeBrochure.get().isEmpty()) {

            if (subCriteria2 != null) {
                subCriteria2 = subCriteria2.or("documentType").is(typeBrochure.get());
            } else {
                subCriteria2 = new Criteria("documentType").is(typeBrochure.get());
            }

        }

        if (typeResearchPaper.isPresent() && !typeResearchPaper.get().isEmpty()) {

            if (subCriteria2 != null) {
                subCriteria2 = subCriteria2.or("documentType").is(typeResearchPaper.get());
            } else {
                subCriteria2 = new Criteria("documentType").is(typeResearchPaper.get());
            }

        }

        if (subCriteria2 == null) {
            subCriteria2 = new Criteria("documentType").is(Config.DOCUMENT_TYPE_TUTORIAL_ORIGINAL_SCRIPT);
        }

        if (subCriteria2 != null) {
            criteria = criteria.subCriteria(subCriteria2);
        }

        CriteriaQuery criteriaQuery = new CriteriaQuery(criteria);
        if (categoryId.isPresent() && categoryId.get() != 0) {
            criteriaQuery.addSort(Sort.by(Sort.Order.asc("orderValue")));
        }

        logger.info("Criteria: {}", criteria);
        SearchHits<DocumentSearch> searchHits = operations.search(criteriaQuery, DocumentSearch.class);

        List<DocumentSearch> documentSearchList = searchHits.stream().map(SearchHit::getContent)
                .collect(Collectors.toList());

        documentSearchMap.put("documentSearchList", documentSearchList);
        logger.info("documentSearchList size: {}", documentSearchList.size());

        for (DocumentSearch ds : documentSearchList) {
            logger.info("Order Value:{}", ds.getOrderValue());
        }
        return documentSearchMap;
    }

}
