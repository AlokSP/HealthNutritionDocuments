package org.spoken_tutorial.health.elasticsearch.repositories;

import java.util.List;

import org.spoken_tutorial.health.elasticsearch.models.QueueManagement;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface QueueManagementRepository extends JpaRepository<QueueManagement, Long> {

    @Query("select max(queueId) from QueueManagement")
    Long getNewId();

    QueueManagement findByQueueId(Long queueId);

    List<QueueManagement> findByStatusOrderByRequestTimeAsc(String status, Limit limit);

    List<QueueManagement> findByStatusOrderByRequestTimeAsc(String status);

}
