package us.nineworlds.serenity.ui.browser.movie;

import android.content.Intent;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.birbit.android.jobqueue.JobManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import javax.inject.Inject;

import us.nineworlds.serenity.core.model.CategoryInfo;
import us.nineworlds.serenity.core.model.SecondaryCategoryInfo;
import us.nineworlds.serenity.core.model.SeriesContentInfo;
import us.nineworlds.serenity.core.model.impl.CategoryMediaContainer;
import us.nineworlds.serenity.core.model.impl.SecondaryCategoryMediaContainer;
import us.nineworlds.serenity.core.model.impl.SeriesMediaContainer;
import us.nineworlds.serenity.core.model.impl.TVCategoryMediaContainer;
import us.nineworlds.serenity.events.MainCategoryEvent;
import us.nineworlds.serenity.events.TVCategoryEvent;
import us.nineworlds.serenity.events.TVCategorySecondaryEvent;
import us.nineworlds.serenity.events.TVShowRetrievalEvent;
import us.nineworlds.serenity.injection.SerenityObjectGraph;
import us.nineworlds.serenity.jobs.TVCategoryJob;
import us.nineworlds.serenity.ui.browser.tv.TVShowBrowserView;

@InjectViewState
public class MovieBrowserPresenter extends MvpPresenter<MovieBrowserView> {

    @Inject
    EventBus eventBus;

    @Inject
    JobManager jobManager;

    public MovieBrowserPresenter() {
        super();
        SerenityObjectGraph.getInstance().inject(this);
    }


    @Override
    public void attachView(MovieBrowserView view) {
        super.attachView(view);
        eventBus.register(this);
    }

    @Override
    public void detachView(MovieBrowserView view) {
        super.detachView(view);
        eventBus.unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMainCategoryResponse(MainCategoryEvent event) {
        CategoryMediaContainer categoryMediaContainer = new CategoryMediaContainer(
                event.getMediaContainer());
        List<CategoryInfo> categories = categoryMediaContainer.createCategories();
        getViewState().populateCategory(categories, event.getKey());
    }


}
