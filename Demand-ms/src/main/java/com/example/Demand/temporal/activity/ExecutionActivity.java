package com.example.Demand.temporal.activity;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface ExecutionActivity {

    @ActivityMethod
    String reserveExecution(int routerId, int operationId, String sfcId, String txnId);
}
