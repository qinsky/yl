package com.youlun.baseframework.core;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.DelegateTask;

public interface Processable {
	/**
	 * 开始执行
	 * @param execution
	 */
	public void startExeNotify(DelegateExecution execution);
	/**
	 * 执行结束
	 * @param execution
	 */
	public void endExeNotify(DelegateExecution execution);
	/**
	 * 流程执行结束
	 * @param execution
	 */
	public void endProcessNotify(DelegateExecution execution);
	/**
	 * 任务创建时候通知
	 * @param delegateTask
	 */
	public void taskCreateNotify(DelegateTask delegateTask);
	/**
	 * 任务完成时通知
	 * @param delegateTask
	 */
	public void taskCompleteNotify(DelegateTask delegateTask);
	/**
	 * 任务分配执行人时通知
	 * @param delegateTask
	 */
	public void taskAssignmentNotify(DelegateTask delegateTask);
}
