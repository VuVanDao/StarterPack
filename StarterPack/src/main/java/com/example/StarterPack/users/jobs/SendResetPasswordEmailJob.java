package com.example.StarterPack.users.jobs;

import org.jobrunr.jobs.lambdas.JobRequest;
import org.jobrunr.jobs.lambdas.JobRequestHandler;

import com.example.StarterPack.users.jobs.handler.SendResetPasswordEmailJobHandler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SendResetPasswordEmailJob implements JobRequest{
    private Long tokenId;

    @Override
    public Class<? extends JobRequestHandler> getJobRequestHandler() {
        System.out.println("----------------------SendResetPasswordEmailJob.getJobRequestHandler-----------------------");
        return SendResetPasswordEmailJobHandler.class;
    }
}
