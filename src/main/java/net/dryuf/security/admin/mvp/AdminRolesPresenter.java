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

package net.dryuf.security.admin.mvp;


import net.dryuf.app.ActionDefImpl;
import net.dryuf.app.FieldDef;
import net.dryuf.app.FieldDefImpl;
import net.dryuf.app.FieldRolesImpl;
import net.dryuf.core.AppContainer;
import net.dryuf.core.CallerContext;
import net.dryuf.core.Options;
import net.dryuf.textual.BoolSwitchTextual;
import net.dryuf.meta.ActionDef;
import net.dryuf.security.UserAccount;
import net.dryuf.security.bo.UserAccountBo;
import net.dryuf.security.web.AuthenticationFrontend;
import net.dryuf.mvp.Presenter;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AdminRolesPresenter extends net.dryuf.mvp.MappedFormPresenter
{
	public				AdminRolesPresenter(Presenter parentPresenter, Options options)
	{
		super(parentPresenter, options);
	}

	@Override
	public boolean			processFinal()
	{
		userAccountBo = getCallerContext().getBeanTyped("userAccountBo", UserAccountBo.class);
		authenticationFrontend = callerContext.getBeanTyped("authenticationFrontend", AuthenticationFrontend.class);

		return super.processFinal();
	}

	@Override
	public void		setBackingObject(Map<String, Object> backingObject)
	{
		super.setBackingObject(backingObject);

		for (String roleName: getCallerContext().getAppContainer().getGlobalRoles()) {
			backingObject.put("role_"+roleName, getCallerContext().checkRole(roleName));
		}

		backingObject.put("translationLevel", getUiContext().getLocalizeDebug());

		backingObject.put("timing", getUiContext().getTiming());

		backingObject.put("effectiveUserId", null);
		backingObject.put("effectiveUserName", null);
	}

	@Override
	public List<FieldDef<?>>	buildDisplayableFields()
	{
		List<FieldDef<?>> fields = new LinkedList<FieldDef<?>>();

		// roles
		for (String roleName: getCallerContext().getAppContainer().getGlobalRoles()) {
			fields.add(
				new FieldDefImpl<Boolean>()
					.setName("role_"+roleName)
					.setDisplay("checkbox()")
					.setMandatory(true)
					.setDoMandatory(false)
					.setTextual(net.dryuf.textual.BoolSwitchTextual.class)
					.setRoles(new FieldRolesImpl(checkRoleDependency(roleName) ? "" : "denied", null, "", null))
			);
		}

		// translation
		fields.add(
			new FieldDefImpl<Integer>()
				.setName("translationLevel")
				.setDisplay("select(120px, none^missing^all)")
				.setMandatory(true)
				.setTextual(net.dryuf.textual.NaturalTextual.class)
				.setRoles(new FieldRolesImpl("translation", null, "", null))
		);

		// timing
		fields.add(
			new FieldDefImpl<Boolean>()
				.setName("timing")
				.setDisplay("checkbox()")
				.setMandatory(true)
				.setDoMandatory(false)
				.setTextual(net.dryuf.textual.BoolSwitchTextual.class)
				.setRoles(new FieldRolesImpl("timing", null, "", null))
		);

		// effective user
		fields.add(
			new FieldDefImpl<Long>()
				.setName("effectiveUserId")
				.setDisplay("text(120px)")
				.setMandatory(false)
				.setTextual(net.dryuf.textual.NaturalLongTextual.class)
				.setRoles(new FieldRolesImpl("swapuser", null, "", null))
		);
		fields.add(
			new FieldDefImpl<String>()
				.setName("effectiveUserName")
				.setDisplay("text(120px)")
				.setMandatory(false)
				.setTextual(net.dryuf.textual.LineTrimTextual.class)
				.setRoles(new FieldRolesImpl("swapuser", null, "", null))
		);

		return fields;
	}

	private boolean			checkRoleDependency(String roleName)
	{
		AppContainer container = getCallerContext().getAppContainer();
		for (String dependent: container.checkRoleDependency(roleName)) {
			if (getCallerContext().checkRole(dependent))
				return true;
		}
		return false;
	}

	@Override
	public void			formOutputType(FieldDef<?> fdef, String d_type, String[] d_args, String formatted)
	{
		super.formOutputType(fdef, d_type, d_args, formatted);
		switch (fdef.getName()) {
		case "effectiveUserId":
			this.outputFormat(" (%S)", this.getCallerContext().getUserId());
			break;

		case "effectiveUserName":
			UserAccount userAccount = userAccountBo.load((Long)this.getCallerContext().getUserId());
			this.outputFormat(" (%S)", userAccount == null ? "unknown" : userAccount.getUsername());
			break;
		}
	}

	@Override
	public void			prepare()
	{
		super.prepare();
	}

	@Override
	public boolean			retrieve(Map<String, String> errors, ActionDef action)
	{
		if (!super.retrieve(errors, action))
			return false;
		if (this.backingObject.get("effectiveUserId") != null) {
			newUserAccount = userAccountBo.load((Integer)this.backingObject.get("effectiveUserId"));
			if (newUserAccount != null) {
			}
			else {
				errors.put("effectiveUserId", this.localize(AdminRolesPresenter.class, "Invalid user ID"));
			}
		}
		else if (this.backingObject.get("effectiveUserName") != null) {
			newUserAccount = userAccountBo.loadByUsername((String)this.backingObject.get("effectiveUserName"));
			if (newUserAccount != null) {
				authenticationFrontend.setEffectiveUserId(this.getPageContext(), newUserAccount.getUserId());
			}
			else {
				errors.put("effectiveUserId", this.localize(AdminRolesPresenter.class, "Invalid user name"));
			}
		}
		return errors.size() == 0;
	}

	@Override
	public boolean			performSubmit(ActionDef action)
	{
		CallerContext callerContext = getCallerContext();

		Set<String> newRoles = new LinkedHashSet<String>();
		for (String roleName: callerContext.getAppContainer().getGlobalRoles()) {
			if ((Boolean)backingObject.get("role_"+roleName))
				newRoles.add(roleName);
		}
		authenticationFrontend.resetRoles(getPageContext(), newRoles);

		authenticationFrontend.setTranslationLevel(getPageContext(), (Integer)backingObject.get("translationLevel"));

		authenticationFrontend.setTiming(getPageContext(), (Boolean)backingObject.get("timing"));

		if (newUserAccount != null)
			authenticationFrontend.setEffectiveUserId(getPageContext(), newUserAccount.getUserId());

		getResponse().redirect(".");
		return false;
	}

	@Override
	public void			render()
	{
		this.output("<h3>Current global roles:</h3><br/>");
		super.render();
	}

	protected UserAccountBo		userAccountBo;

	protected AuthenticationFrontend authenticationFrontend;

	protected UserAccount		newUserAccount;
}
