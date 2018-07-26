/*
 * Kiwix Android
 * Copyright (C) 2018  Kiwix <android.kiwix.org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.kiwix.kiwixmobile.base;

import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import org.kiwix.kiwixmobile.R;
import org.kiwix.kiwixmobile.utils.SharedPreferenceUtil;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

public abstract class BaseActivity extends AppCompatActivity implements HasSupportFragmentInjector {

  @Inject
  protected SharedPreferenceUtil sharedPreferenceUtil;
  @Inject
  DispatchingAndroidInjector<Fragment> fragmentDispatchingAndroidInjector;

  private Unbinder unbinder;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    AndroidInjection.inject(this);
    super.onCreate(savedInstanceState);
  }

  @Override
  public void setContentView(@LayoutRes int layoutResID) {
    if (sharedPreferenceUtil.nightMode()) {
      setTheme(R.style.AppTheme_Night);
    }
    super.setContentView(layoutResID);
    unbinder = ButterKnife.bind(this);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (unbinder != null) {
      unbinder.unbind();
    }
  }

  @Override
  public AndroidInjector<Fragment> supportFragmentInjector() {
    return fragmentDispatchingAndroidInjector;
  }

  public boolean shouldStartNewActivity() {
    int value;
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
      value = Settings.System.getInt(getContentResolver(), Settings.System.ALWAYS_FINISH_ACTIVITIES, 0);
    } else {
      value = Settings.System.getInt(getContentResolver(), Settings.Global.ALWAYS_FINISH_ACTIVITIES, 0);
    }
    return value == 1;
  }
}
