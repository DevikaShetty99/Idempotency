package com.example.execution.temporal.activity;

import com.example.execution.dto.WorkflowInput;
import com.example.execution.dto.WorkflowOutput;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface ExecutionActivity {

    @ActivityMethod
    WorkflowOutput reserveExecution(WorkflowInput input);
}
