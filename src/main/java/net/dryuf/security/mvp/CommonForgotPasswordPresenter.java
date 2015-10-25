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
import net.dryuf.security.form.ForgotPasswordForm;
import net.dryuf.security.UserAccount;
import net.dryuf.security.bo.UserAccountBo;
import net.dryuf.security.dao.UserAccountDao;
import net.dryuf.service.mail.EmailSender;
import net.dryuf.net.util.UrlUtil;


public class CommonForgotPasswordPresenter extends net.dryuf.security.mvp.ForgotPasswordPresenter
{
	public				CommonForgotPasswordPresenter(Presenter parentPresenter, Options options)
	{
		super(parentPresenter, options);

		AppContainer appContainer = getCallerContext().getAppContainer();
		userAccountDao = appContainer.getBeanTyped("userAccountDao", UserAccountDao.class);
		userAccountBo = appContainer.getBeanTyped("userAccountBo", UserAccountBo.class);
		emailSender = appContainer.getBeanTyped("emailSender", EmailSender.class);
	}

	public int			doSend()
	{
		ForgotPasswordForm forgotPasswordForm = getBackingObject();

		UserAccount userAccount;
		if ((userAccount = userAccountDao.loadByUsername(forgotPasswordForm.getUsername())) == null) {
			return 1;
		}

		String code;
		if (!userAccountDao.updateActivity(userAccount.getUserId(), System.currentTimeMillis()+3600000)) {
			return 1;
		}
		if ((code = userAccountBo.getActivityCode(userAccount.getUserId())) == null) {
			return 1;
		}

		emailSender.mailUtf8(userAccount.getEmail(), this.localize(CommonForgotPasswordPresenter.class, "User Forgotten Password"),
				this.localize(CommonForgotPasswordPresenter.class, "You asked for changing the user password on our website. Please change it by clicking on")+" "+UrlUtil.truncateToParent(this.getRootPresenter().getFullUrl())+"forgotupdatepassword/?username="+forgotPasswordForm.getUsername()+"&code="+code,
				null);

		return 0;
	}

	UserAccountDao			userAccountDao;

	UserAccountBo			userAccountBo;

	EmailSender			emailSender;
}
