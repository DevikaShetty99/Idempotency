package com.example.Demand.temporal.activity;

import com.example.Demand.dto.WorkflowInput;
import com.example.Demand.dto.WorkflowOutput;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface ExecutionActivity {

    @ActivityMethod
    WorkflowOutput reserveExecution(WorkflowInput input);
}
