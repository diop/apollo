package io.muun.apollo.presentation.ui.fragments.operations;

import io.muun.apollo.R;
import io.muun.apollo.domain.model.SecurityCenter;
import io.muun.apollo.domain.model.SecurityLevel;
import io.muun.apollo.presentation.ui.adapter.ItemAdapter;
import io.muun.apollo.presentation.ui.adapter.holder.ViewHolderFactory;
import io.muun.apollo.presentation.ui.adapter.viewmodel.ItemViewModel;
import io.muun.apollo.presentation.ui.adapter.viewmodel.OperationViewModel;
import io.muun.apollo.presentation.ui.base.SingleFragment;
import io.muun.apollo.presentation.ui.view.DividerItemDecoration;
import io.muun.apollo.presentation.ui.view.MuunEmptyScreen;
import io.muun.apollo.presentation.ui.view.MuunHeader;

import android.content.Context;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import kotlin.Unit;

import java.util.List;

public class OperationsFragment extends SingleFragment<OperationsPresenter>
        implements OperationsView {

    public static OperationsFragment newInstance() {
        return new OperationsFragment();
    }

    @BindView(R.id.empty_screen)
    MuunEmptyScreen emptyScreen;

    @BindView(R.id.home_operations_recycler_operation_list)
    RecyclerView recyclerView;

    private ItemAdapter adapter;

    @Override
    protected void inject() {
        getComponent().inject(this);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.home_operations_fragment;
    }

    @Override
    protected void initializeUi(View view) {
        super.initializeUi(view);

        final Context context = getContext();

        final MuunHeader header = getParentActivity().getHeader();
        header.attachToActivity(getParentActivity());
        header.clear();
        header.setNavigation(MuunHeader.Navigation.EXIT);
        header.showTitle(R.string.home_operations_list_title);
        header.setElevated(true);

        adapter = new ItemAdapter(new ViewHolderFactory());
        adapter.setOnItemClickListener(this::onItemClicked);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(context, 82, 0));
        recyclerView.setAdapter(adapter);

        emptyScreen.setOnLinkClickListener(ignored -> {
            presenter.goToReceive();
            return Unit.INSTANCE; // Java you suuuuuuck!
        });
    }

    @Override
    public void setViewState(List<ItemViewModel> ops, SecurityCenter sc) {
        final boolean hasOperations = !ops.isEmpty();
        final SecurityLevel securityLevel = sc.getLevel();
        final boolean isAnonUser = securityLevel == SecurityLevel.ANON
                || securityLevel == SecurityLevel.SKIPPED_EMAIL_ANON;

        if (hasOperations) {
            setVisible(recyclerView, true);
            setVisible(emptyScreen, false);

        } else {
            setVisible(recyclerView, false);
            setVisible(emptyScreen, true);
        }

        presenter.reportHome(isAnonUser, hasOperations);

        updateListItems(ops);
    }

    private void updateListItems(List<ItemViewModel> newItems) {
        adapter.setItems(newItems);
    }

    private void onItemClicked(ItemViewModel viewModel) {
        if (viewModel instanceof OperationViewModel) {
            presenter.onOperationClicked(((OperationViewModel) viewModel).operation.getId());
        }
    }
}
