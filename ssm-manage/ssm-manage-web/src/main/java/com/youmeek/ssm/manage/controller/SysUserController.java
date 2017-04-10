package com.youmeek.ssm.manage.controller;


import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.youmeek.ssm.manage.pojo.SysUser;
import com.youmeek.ssm.manage.service.SysUserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
@RequestMapping("/sysUserController")
public class SysUserController {
	@Resource
	private SysUserService sysUserService;

	/**
	 * 根据用户名获取用户对象
	 * @param userId
	 * @return
	 */
	@RequestMapping(value="/user/{userId}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value="根据用户Id获取用户", httpMethod ="GET",response=SysUser.class, notes="根据用户Id获取用户")
	public SysUser getUserById(@ApiParam(required = true, name = "userId", value = "用户Id") @PathVariable("userId") Long userId) throws Exception{

		SysUser user = this.sysUserService.getById(userId);
		if(user != null) {
//			ApiResult<UcUser> result = new ApiResult<UcUser>();
//			result.setCode(ResultCode.SUCCESS.getCode());
//			result.setData(ucUser);
			return user;
		} else {
			throw new Exception("根据{name=" + "" + "}获取不到UcUser对象");
		}
	}



	@RequestMapping("/showUserToJspById/{userId}")
	public String showUser(Model model, @PathVariable("userId") Long userId){
		SysUser user = this.sysUserService.getById(userId);
		model.addAttribute("user", user);
		return "showUser";
	}
	
	@RequestMapping("/showUserToJSONById/{userId}")
	@ResponseBody
	public SysUser showUser(@PathVariable("userId") Long userId){
		SysUser user = sysUserService.getById(userId);
		return user;
	}
	
	



}
