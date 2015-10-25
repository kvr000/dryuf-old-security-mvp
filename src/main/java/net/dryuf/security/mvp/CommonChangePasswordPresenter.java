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

import net.dryuf.core.AppContainer;
import net.dryuf.core.Options;
import net.dryuf.mvp.Presenter;
import net.dryuf.security.form.ChangePasswordForm;
import net.dryuf.security.UserAccount;
import net.dryuf.security.bo.UserAccountBo;
import net.dryuf.security.dao.UserAccountDao;
import net.dryuf.security.web.AuthenticationFrontend;


public class CommonChangePasswordPresenter extends net.dryuf.security.mvp.ChangePasswordPresenter
{
	AuthenticationFrontend		authenticationFrontend;

	UserAccountDao			userAccountDao;

	UserAccountBo			userAccountBo;

	public				CommonChangePasswordPresenter(Presenter parentPresenter, Options options)
	{
		super(parentPresenter, options);

		AppContainer appContainer = getCallerContext().getAppContainer();
		authenticationFrontend = getCallerContext().getBeanTyped("authenticationFrontend", AuthenticationFrontend.class);
		userAccountDao = appContainer.getBeanTyped("userAccountDao", UserAccountDao.class);
		userAccountBo = appContainer.getBeanTyped("userAccountBo", UserAccountBo.class);
	}

	public int			doChange()
	{
		ChangePasswordForm changePasswordForm = getBackingObject();

		UserAccount userAccount;
		if ((userAccount = userAccountDao.loadByPk((Long) this.getCallerContext().getUserId())) == null) {
			return 1;
		}

		int err = authenticationFrontend.authenticateUserPassword(getPageContext(), userAccount.getUsername(), changePasswordForm.getOldPassword());
		if (err != 0)
			return 2;

		userAccountBo.setUserPassword(userAccount, changePasswordForm.getPassword());
		return 0;
	}
};


