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

import javax.validation.constraints.NotNull;

import net.dryuf.core.UiContext;
import net.dryuf.meta.ActionDef;
import net.dryuf.security.admin.form.AdminTranslationForm;
import net.dryuf.security.web.AuthenticationFrontend;
import net.dryuf.mvp.RootPresenter;
import net.dryuf.srvui.Session;


public class AdminTranslationPresenter extends net.dryuf.mvp.BeanFormPresenter<AdminTranslationForm>
{
	public				AdminTranslationPresenter(@NotNull net.dryuf.mvp.Presenter parentPresenter, @NotNull net.dryuf.core.Options options)
	{
		super(parentPresenter, options);
	}

	@Override
	public boolean			processFinal()
	{
		authenticationFrontend = getCallerContext().getBeanTyped("authenticationFrontend", AuthenticationFrontend.class);

		return super.processFinal();
	}

	@Override
	public AdminTranslationForm	createBackingObject()
	{
		return new AdminTranslationForm();
	}

	@Override
	public String			initData()
	{
		Session session = getRootPresenter().getSession();
		if (session != null) {
			getBackingObject().setTranslationLevel(getUiContext().getLocalizeDebug());
			getBackingObject().setTiming(getUiContext().getTiming());
		}
		return null;
	}

	public boolean			performUpdate(ActionDef action)
	{
		RootPresenter rootPresenter = getRootPresenter();
		rootPresenter.getUiContext().setLocalizeDebug(getBackingObject().getTranslationLevel());
		authenticationFrontend.setTranslationLevel(getPageContext(), getBackingObject().getTranslationLevel());
		authenticationFrontend.setTiming(getPageContext(), getBackingObject().getTiming());
		return true;
	}

	@Override
	public void			render()
	{
		this.output("<div class=\"page-key\">Current translation set up:</div>");
		super.render();
	}

	protected AuthenticationFrontend authenticationFrontend;
}
