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

package net.dryuf.security.mvp;

import net.dryuf.core.Dryuf;
import net.dryuf.core.Options;
import net.dryuf.security.bo.UserAccountBo;
import net.dryuf.security.form.LoginForm;
import net.dryuf.mvp.Presenter;
import net.dryuf.security.web.AuthenticationFrontend;


public class CommonLoginPresenter extends net.dryuf.security.mvp.LoginPresenter
{
	public				CommonLoginPresenter(Presenter parentPresenter, Options options)
	{
		super(parentPresenter, options);
		authenticationFrontend = getCallerContext().getBeanTyped("authenticationFrontend", AuthenticationFrontend.class);
	}

	@Override
	public String			doLogin()
	{
		LoginForm loginForm = backingObject;
		int err = authenticationFrontend.authenticateUserPassword(getPageContext(), loginForm.getUsername(), loginForm.getPassword());
		switch (err) {
		case UserAccountBo.ERR_Ok:
			getRootPresenter().getResponse().redirect(loginForm.getRedir() != null ? loginForm.getRedir() : "../logged/");
			return null;

		case UserAccountBo.ERR_UnknownAccount:
			return this.localize(UserAccountBo.class, "Unknown user");

		case UserAccountBo.ERR_WrongPassword:
			return this.localize(UserAccountBo.class, "Wrong password");

		case UserAccountBo.ERR_AccountUnactivated:
			return this.localize(UserAccountBo.class, "User not activated");

		case UserAccountBo.ERR_AccountExpired:
			return this.localize(UserAccountBo.class, "Account expired");

		default:
			return this.localizeArgs(Dryuf.dotClassname(UserAccountBo.class), "Unknown error: {0}", new Object[]{ err });
		}
	}

	protected AuthenticationFrontend authenticationFrontend;
}
