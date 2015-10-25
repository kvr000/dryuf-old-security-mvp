/*
 * Dryuf framework
 *
 * ----------------------------------------------------------------------------------
 *
 * Copyright (C) 2000-2015 Zbyněk Vyškovský
 *
 * ----------------------------------------------------------------------------------
 *
 * LICENSE:
 *
 * This file is part of Dryuf
 *
 * Dryuf is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 *
 * Dryuf is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Dryuf; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * @author	2000-2015 Zbyněk Vyškovský
 * @link	mailto:kvr@matfyz.cz
 * @link	http://kvr.matfyz.cz/software/java/dryuf/
 * @link	http://github.com/dryuf/
 * @license	http://www.gnu.org/licenses/lgpl.txt GNU Lesser General Public License v3
 */

package net.dryuf.security.oper;

import javax.inject.Inject;

import com.google.common.collect.Lists;
import net.dryuf.core.CallerContext;
import net.dryuf.core.ConversionUtil;
import net.dryuf.core.Dryuf;
import net.dryuf.core.EntityHolder;
import net.dryuf.oper.ObjectOperContext;
import net.dryuf.textual.DotIdentifierTextual;
import net.dryuf.security.AppDomainDef;
import net.dryuf.security.AppDomainRole;
import net.dryuf.security.UserAccountDomainRole;
import net.dryuf.security.dao.AppDomainDefDao;
import net.dryuf.security.dao.AppDomainGroupDao;
import net.dryuf.security.dao.AppDomainRoleDao;
import net.dryuf.security.dao.UserAccountDomainRoleDao;
import net.dryuf.security.form.ChangePasswordForm;
import net.dryuf.security.UserAccount;
import net.dryuf.security.bo.UserAccountBo;
import net.dryuf.security.dao.UserAccountDao;
import net.dryuf.util.MapUtil;
import net.dryuf.validation.DataValidatorUtil;
import net.dryuf.oper.DaoObjectOperController;
import net.dryuf.mvp.oper.ObjectOperPresenter;
import net.dryuf.oper.ObjectOperRules;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class UserAccountDomainRoleOper extends DaoObjectOperController<UserAccountDomainRole, UserAccountDomainRole.Pk>
{
	public				UserAccountDomainRoleOper()
	{
		this.setDataClass(UserAccountDomainRole.class);
	}

	protected void			checkDataValidity(CallerContext callerContext, Map<String, Object> data)
	{
		if (data.containsKey("pk")) {
			@SuppressWarnings("unchecked")
			Map<String, Object> pkData = (Map<String, Object>) data.get("pkData");
			if (pkData.containsKey("domain")) {
				String domain = ConversionUtil.convertToClass(String.class, pkData.get("domain"));
				if (!domain.equals(userAccountBo.getAppDomainId()))
					DataValidatorUtil.throwValidationError(null, "pk.domain", callerContext.getUiContext().localizeArgs(UserAccountOper.class, "Cannot set role for different domain: {0}", new Object[]{ domain }));
			}
			if (pkData.containsKey("roleName")) {
				String roleName = ConversionUtil.convertToClass(String.class, pkData.get("roleName"));
				String allowedRole;
				if ((allowedRole = userAccountBo.checkRequiredRoleForRole(callerContext, roleName)) != null)
					DataValidatorUtil.throwValidationError(null, "pk.roleName", callerContext.getUiContext().localizeArgs(UserAccountOper.class, "Cannot assign role {0}, allowed only for {1}", new Object[]{ roleName, allowedRole }));
			}
		}
	}

	@ObjectOperRules(value = "loadRoleRef", reqRole = "free", isStatic = true, isFinal = true, parameters = { DotIdentifierTextual.class })
	public String			loadRoleRef(ObjectOperPresenter presenter, EntityHolder<?> ownerHolder, String roleName)
	{
		return appDomainRoleDao.retrieveDynamic(ownerHolder, new AppDomainRole.Pk(userAccountBo.getAppDomainId(), roleName)).getEntity().getRoleName();
	}

	@ObjectOperRules(value = "listAllRoleRefs", reqRole = "free", isStatic = true, isFinal = true)
	public List<String> listAllRoleRefs(ObjectOperPresenter presenter, EntityHolder<?> ownerHolder)
	{
		EntityHolder<AppDomainDef> domainHolder = appDomainDefDao.retrieveDynamic(ownerHolder, userAccountBo.getAppDomainId());
		List<EntityHolder<AppDomainRole>> results = new LinkedList<EntityHolder<AppDomainRole>>();
		appDomainRoleDao.listDynamic(results, domainHolder, MapUtil.createLinkedHashMap("pk.domain", userAccountBo.getAppDomainId()), null, null, null);
		return Lists.transform(results, (EntityHolder<AppDomainRole> appDomainRole) -> appDomainRole.getEntity().getRoleName());
	}

	@ObjectOperRules(value = "listNewRoleRefs", reqRole = "free", isStatic = true, isFinal = true)
	public Set<String> listNewRoleRefs(ObjectOperPresenter presenter, EntityHolder<?> ownerHolder)
	{
		return userAccountBo.listAddableRoles(ownerHolder.getRole());
	}

	@Override
	public EntityHolder<UserAccountDomainRole> executeStaticCreate(final ObjectOperContext operContext, final EntityHolder<?> ownerHolder, final Map<String, Object> data)
	{
		checkDataValidity(ownerHolder.getRole(), data);
		return super.executeStaticCreate(operContext, ownerHolder, data);
	}

	@Override
	public EntityHolder<UserAccountDomainRole> executeObjectUpdate(final ObjectOperContext operContext, final EntityHolder<UserAccountDomainRole> objectHolder, final Map<String, Object> data)
	{
		checkDataValidity(objectHolder.getRole(), data);
		return super.executeObjectUpdate(operContext, objectHolder, data);
	}

	@Inject
	protected UserAccountBo		userAccountBo;

	@Inject
	protected AppDomainDefDao	appDomainDefDao;

	@Inject
	protected AppDomainGroupDao	appDomainGroupDao;

	@Inject
	protected AppDomainRoleDao	appDomainRoleDao;
}
