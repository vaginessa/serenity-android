/**
 * The MIT License (MIT)
 * Copyright (c) 2012 David Carver
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF
 * OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package us.nineworlds.serenity.ui.browser.movie;

import android.content.SharedPreferences;
import android.view.View;
import dagger.Module;
import dagger.Provides;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.ganin.darv.DpadAwareRecyclerView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;
import us.nineworlds.serenity.BuildConfig;
import us.nineworlds.serenity.TestingModule;
import us.nineworlds.serenity.common.rest.SerenityClient;
import us.nineworlds.serenity.core.model.VideoContentInfo;
import us.nineworlds.serenity.injection.modules.AndroidModule;
import us.nineworlds.serenity.injection.modules.SerenityModule;
import us.nineworlds.serenity.test.InjectingTest;
import us.nineworlds.serenity.ui.adapters.AbstractPosterImageGalleryAdapter;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.robolectric.RuntimeEnvironment.application;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class MovieGridPosterOnItemSelectedListenerTest extends InjectingTest {

  @Inject SerenityClient mockPlexFactory;

  @Mock VideoContentInfo mockVideoContentInfo;

  @Mock SharedPreferences mockPreferences;

  @Mock View mockView;

  @Mock DpadAwareRecyclerView mockRecyclerView;

  @Mock AbstractPosterImageGalleryAdapter mockAdapater;

  MovieGridPosterOnItemSelectedListener onItemSelectedListener;

  MovieBrowserActivity movieBrowserActivity;

  @Override @Before public void setUp() throws Exception {
    ShadowApplication shadowApplication = shadowOf(application);
    shadowApplication.declareActionUnbindable("com.google.android.gms.analytics.service.START");

    MockitoAnnotations.initMocks(this);
    super.setUp();
    doReturn(true).when(mockPreferences).getBoolean("movie_layout_grid", false);

    onItemSelectedListener = new MovieGridPosterOnItemSelectedListener();
    movieBrowserActivity = Robolectric.buildActivity(MovieBrowserActivity.class).create().start().get();
    doReturn(movieBrowserActivity).when(mockView).getContext();
  }

  @After public void tearDown() {
    if (movieBrowserActivity != null) {
      movieBrowserActivity.finish();
    }
  }

  @Test public void whenItemIsSelectedTheBackgroundIsChanged() {
    String expectedBackgroundUrl = "http://www.example.com/some/image";
    doReturn(expectedBackgroundUrl).when(mockVideoContentInfo).getBackgroundURL();
    String expectedTranscodingUrl = "http://www.example.com/transcodingUrl";
    doReturn(expectedTranscodingUrl).when(mockPlexFactory).getImageURL(anyString(), anyInt(), anyInt());
    doReturn(mockAdapater).when(mockRecyclerView).getAdapter();
    doReturn(2).when(mockAdapater).getItemCount();
    doReturn(mockVideoContentInfo).when(mockAdapater).getItem(anyInt());

    onItemSelectedListener.onItemSelected(mockRecyclerView, mockView, 0, 0);

    verifyExpectedFanArtCalls(expectedBackgroundUrl, expectedTranscodingUrl);
  }

  protected void verifyExpectedFanArtCalls(String expectedBackgroundUrl, String expectedTranscodingUrl) {
    verify(mockVideoContentInfo, times(2)).getBackgroundURL();
    verify(mockPlexFactory).getImageURL(expectedBackgroundUrl, 1280, 720);
  }

  @Override public List<Object> getModules() {
    List<Object> modules = new ArrayList<Object>();
    modules.add(new AndroidModule(application));
    modules.add(new TestingModule());
    modules.add(new TestModule());

    return modules;
  }

  @Module(includes = SerenityModule.class, addsTo = AndroidModule.class, overrides = true, injects = {
      MovieGridPosterOnItemSelectedListener.class, MovieGridPosterOnItemSelectedListenerTest.class
  })
  public class TestModule {

    @Provides @Singleton SharedPreferences providesSharedPreferences() {
      return mockPreferences;
    }
  }
}
