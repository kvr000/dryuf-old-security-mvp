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
import net.dryuf.dao.DaoUniqueConstraintException;
import net.dryuf.mvp.Presenter;
import net.dryuf.security.form.RegisterForm;
import net.dryuf.security.UserAccount;
import net.dryuf.security.bo.UserAccountBo;
import net.dryuf.security.dao.UserAccountDao;
import net.dryuf.service.mail.EmailSender;
import net.dryuf.net.util.UrlUtil;


public class CommonRegisterPresenter extends net.dryuf.security.mvp.RegisterPresenter
{
	public CommonRegisterPresenter(Presenter parentPresenter, Options options)
	{
		super(parentPresenter, options);

		AppContainer appContainer = getCallerContext().getAppContainer();
		userAccountBo = appContainer.getBeanTyped("userAccountBo", UserAccountBo.class);
		emailSender = appContainer.getBeanTyped("emailSender", EmailSender.class);
	}

	public int			doRegister()
	{
		RegisterForm registerForm = backingObject;

		UserAccount userAccount = new UserAccount();
		userAccount.setUsername(registerForm.getUsername());
		userAccount.setEmail(registerForm.getEmail());

		try {
			this.userAccountBo.createUser(userAccount, registerForm.getPassword());
		}
		catch (DaoUniqueConstraintException ex) {
			this.addMessage(Presenter.MSG_Error, this.localize(CommonRegisterPresenter.class, "User with the same name already exists, please choose different name"));
			return 1;
		}
		emailSender.mailUtf8(userAccount.getEmail(), this.localize(CommonRegisterPresenter.class, "User Registration"),
				this.localize(CommonRegisterPresenter.class, "You asked for creating the user on our website. Please activate it clicking on")+" "+UrlUtil.truncateToParent(this.getRootPresenter().getFullUrl())+"registeractivate/?username="+userAccount.getUsername()+"&code="+userAccountBo.getActivityCode(userAccount.getUserId()),
				null);

		return 0;
	}

	protected UserAccountBo		userAccountBo;

	protected EmailSender		emailSender;
}
