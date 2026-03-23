package com.example.StarterPack.users.jobs;

import org.jobrunr.jobs.lambdas.JobRequest;
import org.jobrunr.jobs.lambdas.JobRequestHandler;

import com.example.StarterPack.users.jobs.handler.SendWelcomeEmailJobHandler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SendWelcomeEmailJob implements JobRequest {
    // JobRequest = "việc cần làm"
    // Handler (SendWelcomeEmailJobHandler) = "người làm việc"
    // JobRunr = "manager giao việc"
    private Long userId;
    @Override
    public Class<? extends JobRequestHandler> getJobRequestHandler() {
        System.out.println("----------------------SendWelcomeEmailJob.getJobRequestHandler-----------------------");
        // getJobRequestHandler() để làm gì?
        // Hiểu đơn giản, Nó nói với JobRunr
        // "Muốn xử lý job này → dùng class này"
        return SendWelcomeEmailJobHandler.class;
    }
    
}
