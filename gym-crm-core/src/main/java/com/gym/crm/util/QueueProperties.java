package com.gym.crm.util;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class QueueProperties {
    @Value("${jms.queues.trainer-workload:trainer.workload.queue}")
    private String trainerWorkloadQueue;
}
