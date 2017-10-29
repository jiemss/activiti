package com.jiem.activiti.variable;

import java.util.Date;
import java.util.List;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.junit.Test;

public class Variables {

	//流程变量
	private ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	
	@Test  // 部署流程定义
	public void deployProcessDefination() {
		Deployment deploy = processEngine.getRepositoryService()
				.createDeployment().name("支付流程")
				.addClasspathResource("diagrams/AppayBill.bpmn")
				.addClasspathResource("diagrams/AppayBill.png")
				.deploy();

		System.out.println("部署名称:" + deploy.getName());
		System.out.println("部署id:" + deploy.getId());
	}
	
	@Test  // 执行流程
	public void startProcess(){
		String processDefiKey = "appayBill";
		ProcessInstance pi = processEngine.getRuntimeService()
				.startProcessInstanceByKey(processDefiKey);
		
		System.out.println("流程执行对象的id：" + pi.getId());// Execution 对象
		System.out.println("流程实例的id：" + pi.getProcessInstanceId());// ProcessInstance 对象
		System.out.println("流程定义的id：" + pi.getProcessDefinitionId());// 默认执行的是最新版本的流程定义
	}
	
	@Test  // 查询正在运行任务
	public void queryTask() {
		TaskService taskService = processEngine.getTaskService();
		TaskQuery taskQuery = taskService.createTaskQuery();
		List<org.activiti.engine.task.Task> list = taskQuery.list();
		if (list != null && list.size() > 0) {
			for (Task task : list) {
				System.out.println("任务的办理人：" + task.getAssignee());
				System.out.println("任务的id：" + task.getId());
				System.out.println("任务的名称：" + task.getName());
			}
		}
	}
	
	@Test   // 完成任务
	public void compileTask() {
		String taskId = "1904";
		processEngine.getTaskService().complete(taskId);
		System.out.println("当前任务执行完毕");
	}
	
	@Test 
	public void setVariable(){
		String taskId = "2002";
		
		//1. 第一次设置流程变量
//		TaskService taskService = processEngine.getTaskService();
//		taskService.setVariable(taskId, "cost", 1000);//设置单一的变量，作用域在整个流程实例
//		taskService.setVariable(taskId, "申请时间", new Date());
//		taskService.setVariableLocal(taskId, "申请人", "何某某");//该变量只有在本任务中是有效的
		
		
		//2. 在不同的任务中设置变量
//		TaskService taskService = processEngine.getTaskService();
//		taskService.setVariable(taskId, "cost", 5000);//设置单一的变量，作用域在整个流程实例
//		taskService.setVariable(taskId, "申请时间", new Date());
//		taskService.setVariableLocal(taskId, "申请人", "李某某");//该变量只有在本任务中是有效的
		
		//3. 变量支持的类型
		TaskService taskService = processEngine.getTaskService();
		AppayBillBean appayBillBean=new AppayBillBean();  //传递的一个自定义bean对象
		appayBillBean.setId(1);
		appayBillBean.setCost(300);
		appayBillBean.setDate(new Date());
		appayBillBean.setAppayPerson("何某某");
		taskService.setVariable(taskId, "appayBillBean", appayBillBean);
		
		System.out.println("设置成功！");
	}
	
	
	@Test  //查询流程变量
	public void getVariable(){
		String taskId="2002";//任务id
		
		TaskService taskService = processEngine.getTaskService();
		Integer cost=(Integer) taskService.getVariable(taskId, "cost");//取变量
		Date date=(Date) taskService.getVariable(taskId, "申请时间");//取本任务中的变量
		String appayPerson=(String) taskService.getVariableLocal(taskId, "申请人");//取本任务中的变量
		
		System.out.println("金额:"+cost);
		System.out.println("申请时间:"+date);
		System.out.println("申请人:"+appayPerson);
		
		
		//读取实现序列化的对象变量数据
		AppayBillBean appayBillBean=(AppayBillBean) taskService.getVariable(taskId, "appayBillBean");
		
		System.out.println(appayBillBean.getCost());
		System.out.println(appayBillBean.getAppayPerson());
	}

	@Test  //模拟流程变量设置
	public void  getAndSetProcessVariable(){
		//有两种服务可以设置流程变量
//		TaskService taskService = processEngine.getTaskService();
//		RuntimeService runtimeService = processEngine.getRuntimeService();
		
		/**1.通过 runtimeService 来设置流程变量
		 * exxcutionId: 执行对象
		 * variableName：变量名
		 * values：变量值
		 */
//		runtimeService.setVariable(exxcutionId, variableName, values);
//		runtimeService.setVariableLocal(executionId, variableName, values);
		//设置本执行对象的变量 ，该变量的作用域只在当前的execution对象
//		runtimeService.setVariables(exxcutionId, variables); 
		  //可以设置多个变量  放在 Map<key,value>  Map<String,Object>
		
		/**2. 通过TaskService来设置流程变量
		 * taskId：任务id
		 */
//		taskService.setVariable(taskId, variableName, values);
//		taskService.setVariableLocal(taskId, variableName, values);
////		设置本执行对象的变量 ，该变量的作用域只在当前的execution对象
//		taskService.setVariables(taskId, variables); //设置的是Map<key,values>
		
		/**3. 当流程开始执行的时候，设置变量参数
		 * processDefiKey: 流程定义的key
		 * variables： 设置多个变量  Map<key,values>
		 */
//		processEngine.getRuntimeService()
//		.startProcessInstanceByKey(processDefiKey, variables)
		
		/**4. 当执行任务时候，可以设置流程变量
		 * taskId:任务id
		 * variables： 设置多个变量  Map<key,values>
		 */
//		processEngine.getTaskService().complete(taskId, variables);
		
		
		/** 5. 通过RuntimeService取变量值
		 * exxcutionId： 执行对象
		 * 
		 */
//		runtimeService.getVariable(exxcutionId, variableName);//取变量
//		runtimeService.getVariableLocal(exxcutionId, variableName);//取本执行对象的某个变量
//		runtimeService.getVariables(variablesName);//取当前执行对象的所有变量
		/** 6. 通过TaskService取变量值
		 * TaskId： 执行对象
		 * 
		 */
//		taskService.getVariable(taskId, variableName);//取变量
//		taskService.getVariableLocal(taskId, variableName);//取本执行对象的某个变量
//		taskService.getVariables(taskId);//取当前执行对象的所有变量
	}

	
	
}
