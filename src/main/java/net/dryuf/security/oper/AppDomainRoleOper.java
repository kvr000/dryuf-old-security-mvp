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
import net.dryuf.textual.DotIdentifierTextual;
import net.dryuf.security.AppDomainRole;
import net.dryuf.security.dao.AppDomainRoleDao;
import net.dryuf.security.bo.UserAccountBo;
import net.dryuf.util.MapUtil;
import net.dryuf.oper.DaoObjectOperController;
import net.dryuf.mvp.oper.ObjectOperPresenter;
import net.dryuf.oper.ObjectOperRules;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;


public class AppDomainRoleOper extends DaoObjectOperController<AppDomainRole, AppDomainRole.Pk>
{
	public				AppDomainRoleOper()
	{
		this.setDataClass(AppDomainRole.class);
	}

	@ObjectOperRules(value = "loadAppSpecific", reqRole = "free", isStatic = true, isFinal = true, parameters = { DotIdentifierTextual.class })
	public String			loadAppSpecific(ObjectOperPresenter presenter, EntityHolder<?> ownerHolder, String roleName)
	{
		return appDomainRoleDao.retrieveDynamic(ownerHolder, new AppDomainRole.Pk(userAccountBo.getAppDomainId(), roleName)).getEntity().getRoleName();
	}

	@ObjectOperRules(value = "listAllRoles", reqRole = "free", isStatic = true, isFinal = true)
	public List<String>		listAllRoles(ObjectOperPresenter presenter, EntityHolder<?> ownerHolder)
	{
		List<EntityHolder<AppDomainRole>> results = new LinkedList<EntityHolder<AppDomainRole>>();
		appDomainRoleDao.listDynamic(results, ownerHolder, MapUtil.createLinkedHashMap("appDomain", userAccountBo.getAppDomainId()), null, null, null);
		return Lists.transform(results, (EntityHolder<AppDomainRole> appDomainRole) -> appDomainRole.getEntity().getRoleName());
	}

	@ObjectOperRules(value = "listOperableRoles", reqRole = "free", isStatic = true, isFinal = true)
	public Set<String>		listOperableRoles(ObjectOperPresenter presenter, EntityHolder<?> ownerHolder)
	{
		return userAccountBo.listAddableRoles(ownerHolder.getRole());
	}

	@Inject
	protected UserAccountBo		userAccountBo;

	@Inject
	protected AppDomainRoleDao	appDomainRoleDao;
}
