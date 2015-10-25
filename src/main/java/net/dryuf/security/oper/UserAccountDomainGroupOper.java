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
import net.dryuf.core.EntityHolder;
import net.dryuf.oper.ObjectOperContext;
import net.dryuf.textual.DotIdentifierTextual;
import net.dryuf.security.AppDomainDef;
import net.dryuf.security.AppDomainGroup;
import net.dryuf.security.UserAccountDomainGroup;
import net.dryuf.security.dao.AppDomainDefDao;
import net.dryuf.security.dao.AppDomainGroupDao;
import net.dryuf.security.dao.AppDomainRoleDao;
import net.dryuf.security.bo.UserAccountBo;
import net.dryuf.util.MapUtil;
import net.dryuf.validation.DataValidatorUtil;
import net.dryuf.oper.DaoObjectOperController;
import net.dryuf.mvp.oper.ObjectOperPresenter;
import net.dryuf.oper.ObjectOperRules;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class UserAccountDomainGroupOper extends DaoObjectOperController<UserAccountDomainGroup, UserAccountDomainGroup.Pk>
{
	public				UserAccountDomainGroupOper()
	{
		this.setDataClass(UserAccountDomainGroup.class);
	}

	@SuppressWarnings("unchecked")
	protected void			checkDataValidity(CallerContext callerContext, Map<String, Object> data)
	{
		if (data.containsKey("pk")) {
			Map<String, Object> pkData = (Map<String, Object>) data.get("pkData");
			if (pkData.containsKey("domain")) {
				String domain = ConversionUtil.convertToClass(String.class, pkData.get("domain"));
				if (!domain.equals(userAccountBo.getAppDomainId()))
					DataValidatorUtil.throwValidationError(null, "pk.domain", callerContext.getUiContext().localizeArgs(UserAccountOper.class, "Cannot set role for different domain: {0}", new Object[]{ domain }));
			}
			if (pkData.containsKey("groupName")) {
				String groupName = ConversionUtil.convertToClass(String.class, pkData.get("groupName"));
				String allowedRole;
				if ((allowedRole = userAccountBo.checkRequiredRoleForGroup(callerContext, groupName)) != null)
					DataValidatorUtil.throwValidationError(null, "pk.groupName", callerContext.getUiContext().localizeArgs(UserAccountOper.class, "Cannot assign role {0}, allowed only for {1}", new Object[]{ groupName, allowedRole }));
			}
		}
	}

	@ObjectOperRules(value = "loadGroupRef", reqRole = "free", isStatic = true, isFinal = true, parameters = { DotIdentifierTextual.class })
	public String			loadGroupRef(ObjectOperPresenter presenter, EntityHolder<?> ownerHolder, String roleName)
	{
		return appDomainGroupDao.retrieveDynamic(ownerHolder, new AppDomainGroup.Pk(userAccountBo.getAppDomainId(), roleName)).getEntity().getGroupName();
	}

	@ObjectOperRules(value = "listAllGroupRefs", reqRole = "free", isStatic = true, isFinal = true)
	public List<String>		listAllGroupRefs(ObjectOperPresenter presenter, EntityHolder<?> ownerHolder)
	{
		EntityHolder<AppDomainDef> domainHolder = appDomainDefDao.retrieveDynamic(ownerHolder, userAccountBo.getAppDomainId());
		List<EntityHolder<AppDomainGroup>> results = new LinkedList<EntityHolder<AppDomainGroup>>();
		appDomainGroupDao.listDynamic(results, domainHolder, MapUtil.createLinkedHashMap("pk.domain", userAccountBo.getAppDomainId()), null, null, null);
		return Lists.transform(results, (EntityHolder<AppDomainGroup> appDomainGroup) -> appDomainGroup.getEntity().getGroupName());
	}

	@ObjectOperRules(value = "listNewGroupRefs", reqRole = "free", isStatic = true, isFinal = true)
	public Set<String> listNewGroupRefs(ObjectOperPresenter presenter, EntityHolder<?> ownerHolder)
	{
		return userAccountBo.listAddableGroups(ownerHolder.getRole());
	}

	@Override
	public EntityHolder<UserAccountDomainGroup> executeStaticCreate(final ObjectOperContext operContext, final EntityHolder<?> ownerHolder, final Map<String, Object> data)
	{
		checkDataValidity(ownerHolder.getRole(), data);
		return super.executeStaticCreate(operContext, ownerHolder, data);
	}

	@Override
	public EntityHolder<UserAccountDomainGroup> executeObjectUpdate(final ObjectOperContext operContext, final EntityHolder<UserAccountDomainGroup> objectHolder, final Map<String, Object> data)
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
