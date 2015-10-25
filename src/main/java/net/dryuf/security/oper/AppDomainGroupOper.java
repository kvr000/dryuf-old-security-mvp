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
import net.dryuf.core.EntityHolder;
import net.dryuf.oper.ObjectOperRules;
import net.dryuf.textual.DotIdentifierTextual;
import net.dryuf.security.AppDomainGroup;
import net.dryuf.security.dao.AppDomainGroupDao;
import net.dryuf.security.bo.UserAccountBo;
import net.dryuf.util.MapUtil;
import net.dryuf.oper.DaoObjectOperController;
import net.dryuf.mvp.oper.ObjectOperPresenter;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;


public class AppDomainGroupOper extends DaoObjectOperController<AppDomainGroup, AppDomainGroup.Pk>
{
	public				AppDomainGroupOper()
	{
		this.setDataClass(AppDomainGroup.class);
	}

	@ObjectOperRules(value = "loadAppSpecific", reqRole = "free", isStatic = true, isFinal = true, parameters = { DotIdentifierTextual.class })
	public String			 loadAppSpecific(ObjectOperPresenter presenter, EntityHolder<?> ownerHolder, String roleName)
	{
		return appDomainGroupDao.retrieveDynamic(ownerHolder, new AppDomainGroup.Pk(userAccountBo.getAppDomainId(), roleName)).getEntity().getGroupName();
	}

	@ObjectOperRules(value = "listAllGroups", reqRole = "free", isStatic = true, isFinal = true)
	public List<String>		listAllGroups(ObjectOperPresenter presenter, EntityHolder<?> ownerHolder)
	{
		List<EntityHolder<AppDomainGroup>> results = new LinkedList<EntityHolder<AppDomainGroup>>();
		appDomainGroupDao.listDynamic(results, ownerHolder, MapUtil.createLinkedHashMap("appDomain", userAccountBo.getAppDomainId()), null, null, null);
		return Lists.transform(results, (EntityHolder<AppDomainGroup> appDomainGroup) -> appDomainGroup.getEntity().getGroupName());
	}

	@ObjectOperRules(value = "listOperableGroups", reqRole = "free", isStatic = true, isFinal = true)
	public Set<String>		listOperableGroups(ObjectOperPresenter presenter, EntityHolder<?> ownerHolder)
	{
		return userAccountBo.listAddableGroups(ownerHolder.getRole());
	}


	@Inject
	protected UserAccountBo		userAccountBo;

	@Inject
	protected AppDomainGroupDao	appDomainGroupDao;
}
