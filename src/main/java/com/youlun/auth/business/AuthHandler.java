package com.youlun.auth.business;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.youlun.auth.util.AuthBusinessUtils;
import com.youlun.baseframework.core.AbstractBusinessHandler;
import com.youlun.baseframework.core.RequestContext;
import com.youlun.baseframework.core.RunContext;
import com.youlun.baseframework.dao.DBOperator;
import com.youlun.baseframework.util.MD5;
import com.youlun.baseframework.util.StringUtils;
import com.youlun.menu.util.MenuBusinessUtils;

@Component("auth")
@Scope("prototype")
public class AuthHandler  extends AbstractBusinessHandler{
 
	public void main() throws ServletException, IOException 
	{ 
		request.getRequestDispatcher("/module/bsf/bsf/main.jsp").forward(request, response);
	}
	
	public void menu()
	{
		String userId = RunContext.getInstance().getUserId();
		JSONArray tArray = null;
		if("6075147cd0e44364b7dc064ae9c80c2c".equals(userId))
		{
			tArray = AuthBusinessUtils.getAdminMenu();
		}else
		{
			tArray = AuthBusinessUtils.getUserMenu(userId);
		}
		
		success(tArray);
	}
	
	public void openMenu() throws IOException, ServletException
	{ 
		long begin = System.currentTimeMillis();
		String userId = RunContext.getInstance().getUserId();
		String menuId = params.getString("menu_id");
		
		RequestContext.setAttribute("menuId", menuId);
		if(params.has("iniBillId"))
		{
			RequestContext.setAttribute("iniBillId", params.get("iniBillId"));
		}
		
		
		if(StringUtils.isEmpty(menuId))
		{
			response.sendError(404);
			return;
		}
		
		//系统管理员不做校验
		if(!"6075147cd0e44364b7dc064ae9c80c2c".equals(userId))
		{
			if(!AuthBusinessUtils.checkMenuPrivilege(userId, menuId))
			{
				response.sendError(403);
				return;
			}
		}
		
		
		String path = MenuBusinessUtils.getMenuViewPath(menuId);
		if(path==null)
		{ 
			response.sendError(404);
			return;
		}
		
		//如果使用默认单据页面，需要加载相关的资源
		Object dv = RequestContext.getAttribute("defaultView");
		if(dv!=null && ((Boolean)dv))
		{
			List<String> rs = MenuBusinessUtils.getMenuResource(menuId);
			RequestContext.setAttribute("resource", rs);
		}
		log.debug("耗时:"+(System.currentTimeMillis()-begin));
		request.getRequestDispatcher(path).forward(request, response);
	}
	
	/**修改密码*/
	public void modifyPs()
	{
		String userId = RunContext.getInstance().getUserId();
		String newps = params.getString("field2");
		String oldps = params.getString("field1");
		if(StringUtils.isEmpty(oldps))
		{
			error("旧密码不能为空!");
			return;
		}
		if(StringUtils.isEmpty(newps))
		{
			error("新密码不能为空!");
			return;
		}
		if(newps.length()<6)
		{
			error("新密码长度不能小于六位!");
			return;
		}
		newps = MD5.encrypt(newps.trim());
		oldps = MD5.encrypt(oldps.trim());
		
		JSONObject user = DBOperator.queryForSingle("select id,password from ul_bsf_user where df!='Y' and id=?",new String[]{userId});
		if(!oldps.equals(user.get("password")))
		{
			error("旧密码错误!");
			return;
		}
		user.put("password", newps);
		
		//更新密码
		DBOperator.updateByPk("ul_bsf_user", user);
		success("修改密码成功!");
	}
	
	
}
