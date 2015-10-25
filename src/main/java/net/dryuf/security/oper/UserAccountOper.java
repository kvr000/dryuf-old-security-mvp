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

import net.dryuf.core.CallerContext;
import net.dryuf.core.Dryuf;
import net.dryuf.core.EntityHolder;
import net.dryuf.oper.ObjectOperContext;
import net.dryuf.security.form.ChangePasswordForm;
import net.dryuf.security.UserAccount;
import net.dryuf.security.bo.UserAccountBo;
import net.dryuf.security.dao.UserAccountDao;
import net.dryuf.validation.DataValidatorUtil;
import net.dryuf.oper.DaoObjectOperController;
import net.dryuf.mvp.oper.ObjectOperPresenter;
import net.dryuf.oper.ObjectOperRules;

import java.util.Map;


public class UserAccountOper extends DaoObjectOperController<UserAccount, Long>
{
	public				UserAccountOper()
	{
		this.setDataClass(UserAccount.class);
	}

	protected void			checkDataValidity(CallerContext callerContext, Map<String, Object> data)
	{
//		if (data.containsKey("sysRoles")) {
//			// make sure
//			long roles = ConversionUtil.convertToClass(Long.class, data.get("sysRoles"));
//			for (long i = 1; roles != 0; i <<= 1) {
//				if ((i&roles) != 0) {
//					String roleName = userAccountBo.formatGlobalRole(i);
//					if (!callerContext.checkRole(roleName))
//						DataValidatorUtil.throwValidationError(null, "sysRoles", callerContext.getUiContext().localizeArgs(UserAccountOper.class, "Cannot assign different role to what current user currently has: {0}", new Object[]{ roleName }));
//					roles &= i;
//				}
//			}
//		}
	}

	@Override
	public EntityHolder<UserAccount> executeStaticCreate(final ObjectOperContext operContext, final EntityHolder<?> ownerHolder, final Map<String, Object> data)
	{
		checkDataValidity(ownerHolder.getRole(), data);
		return super.executeStaticCreate(operContext, ownerHolder, data);
	}

	@Override
	public EntityHolder<UserAccount> executeObjectUpdate(final ObjectOperContext operContext, final EntityHolder<UserAccount> objectHolder, final Map<String, Object> data)
	{
		checkDataValidity(objectHolder.getRole(), data);
		return super.executeObjectUpdate(operContext, objectHolder, data);
	}

	@ObjectOperRules(value = "changeSysPassword", reqRole = "free", isStatic = true, isFinal = true, actionClass = ChangePasswordForm.class)
	public Object			changeSysPassword(ObjectOperPresenter presenter, EntityHolder<?> ownerHolder, ChangePasswordForm changePasswordForm)
	{
		UserAccount userAccount = userAccountDao.loadByPk((Long) presenter.getCallerContext().getUserId());
		Dryuf.assertNotNull(userAccount, "userAccount cannot be null");
		if (!changePasswordForm.getPassword().equals(changePasswordForm.getPassword2())) {
			DataValidatorUtil.throwValidationError(changePasswordForm, "password2", presenter.localize(UserAccountDomainRoleOper.class, "Passwords do not match"));
		}
		if (userAccountBo.checkUserPassword(userAccount.getUserId(), changePasswordForm.getOldPassword()) != 0) {
			DataValidatorUtil.throwValidationError(changePasswordForm, "oldPassword", presenter.localize(UserAccountDomainRoleOper.class, "Old password is wrong"));
		}
		userAccountBo.setUserPassword(userAccount, changePasswordForm.getPassword());
		return new SuccessContainer(true);
	}

	@Inject
	protected UserAccountDao	userAccountDao;

	@Inject
	protected UserAccountBo		userAccountBo;
}
