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
import net.dryuf.security.UserAccount;
import net.dryuf.security.bo.UserAccountBo;
import net.dryuf.security.dao.UserAccountDao;
import net.dryuf.mvp.Presenter;


public class CommonRegisterActivatePresenter extends net.dryuf.security.mvp.RegisterActivatePresenter
{
	public CommonRegisterActivatePresenter(Presenter parentPresenter, Options options)
	{
		super(parentPresenter, options);

		AppContainer appContainer = getCallerContext().getAppContainer();
		userAccountDao = appContainer.getBeanTyped("userAccountDao", UserAccountDao.class);
		userAccountBo = appContainer.getBeanTyped("userAccountBo", UserAccountBo.class);
	}

	@Override
	public int			doActivate(String username, String activationCode)
	{
		UserAccount userAccount;

		if ((userAccount = userAccountDao.loadByUsername(username)) == null)
			return UserAccountBo.ERR_UnknownAccount;

		String code;
		if ((code = userAccountBo.getActivityCode(userAccount.getUserId())) == null) {
			return UserAccountBo.ERR_BadActivationCode;
		}
		if (!code.equals(activationCode))
			return UserAccountBo.ERR_BadActivationCode;

		if (!userAccountDao.activateUser(userAccount.getUserId(), System.currentTimeMillis()))
			throw new RuntimeException("failed to activate user");
		return 0;
	}

	UserAccountDao			userAccountDao;

	UserAccountBo			userAccountBo;
}
