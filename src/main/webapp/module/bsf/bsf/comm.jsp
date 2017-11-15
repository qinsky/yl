		<link rel="stylesheet" type="text/css" href="${ctxPath}/easyui/themes/default/easyui.css">
		<link rel="stylesheet" type="text/css" href="${ctxPath}/easyui/themes/icon.css">
		<!--  
		<link rel="stylesheet" type="text/css" href="${ctxPath}/easyui/demo/demo.css"> -->
		  <script> 
			var cxtPath = "${ctxPath}/facade.do"; 
			var qq9214Bt = "${cur_qq9214_bt}"; 
			var menuId = "${menuId}";
			var menuName = "${menuName}";
			var iniBillId = "${iniBillId}";
		 </script>
		 <script type="text/javascript" src="${ctxPath}/easyui/jquery.min.js"></script>
		 <script type="text/javascript" src="${ctxPath}/easyui/jquery.easyui.min.js"></script>
		 <script type="text/javascript" src="${ctxPath}/easyui/jquery.blockUI.js"></script> 
		 <script type="text/javascript" src="${ctxPath}/easyui/locale/easyui-lang-zh_CN.js"></script> 
		 <script type="text/javascript" src="${ctxPath}/module/bsf/bsf/view.js?v=<%=com.youlun.baseframework.util.AppUtils.getResLastTimeStr("/module/bsf/bsf/view.js")%>" ></script>
		 <script type="text/javascript" src="${ctxPath}/module/bsf/bsf/utils.js?v=<%=com.youlun.baseframework.util.AppUtils.getResLastTimeStr("/module/bsf/bsf/utils.js")%>"></script> 
		 <script type="text/javascript" src="${ctxPath}/module/bsf/bsf/extend.easyui.js?v=<%=com.youlun.baseframework.util.AppUtils.getResLastTimeStr("/module/bsf/bsf/extend.easyui.js")%>"></script> 
		 <script type="text/javascript" src="${ctxPath}/module/bsf/bsf/appctx.js?v=<%=com.youlun.baseframework.util.AppUtils.getResLastTimeStr("/module/bsf/bsf/appctx.js")%>"></script> 
		 <script type="text/javascript" src="${ctxPath}/module/bsf/bsf/model.js?v=<%=com.youlun.baseframework.util.AppUtils.getResLastTimeStr("/module/bsf/bsf/model.js")%>"></script> 
		 <script type="text/javascript" src="${ctxPath}/module/bsf/bsf/view.refPanel.js?v=<%=com.youlun.baseframework.util.AppUtils.getResLastTimeStr("/module/bsf/bsf/view.refPanel.js")%>"></script>
		 <script type="text/javascript" src="${ctxPath}/module/bsf/bsf/cache.js?v=<%=com.youlun.baseframework.util.AppUtils.getResLastTimeStr("/module/bsf/bsf/cache.js")%>"></script>
		 <script type="text/javascript" src="${ctxPath}/module/bsf/bsf/query.js?v=<%=com.youlun.baseframework.util.AppUtils.getResLastTimeStr("/module/bsf/bsf/query.js")%>"></script>  
		 <script type="text/javascript" src="${ctxPath}/module/bsf/bsf/handler.js?v=<%=com.youlun.baseframework.util.AppUtils.getResLastTimeStr("/module/bsf/bsf/handler.js")%>"></script>  
		 <script type="text/javascript" src="${ctxPath}/module/bsf/bsf/initial.js?v=<%=com.youlun.baseframework.util.AppUtils.getResLastTimeStr("/module/bsf/bsf/initial.js")%>"></script>
		 
		 <c:forEach var="res"   items="${resource}"> 
			<c:if test='${fn:indexOf(res, ".js")!=-1}'>
				 <script type="text/javascript" src="${ctxPath}/${res}"></script>
			</c:if>
			<c:if test='${fn:indexOf(res, ".css")!=-1}'>
		 		<link rel="stylesheet" type="text/css" href="${ctxPath}${res}" >
			</c:if>
		 </c:forEach>  
 