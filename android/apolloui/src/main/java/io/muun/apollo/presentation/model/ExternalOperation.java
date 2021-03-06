package io.muun.apollo.presentation.model;

import io.muun.apollo.R;
import io.muun.apollo.domain.model.CurrencyDisplayMode;
import io.muun.apollo.domain.model.Operation;
import io.muun.apollo.presentation.ui.utils.LinkBuilder;
import io.muun.apollo.presentation.ui.utils.UiUtils;

import android.content.Context;
import org.bitcoinj.core.NetworkParameters;

public class ExternalOperation extends UiOperation {

    /**
     * Constructor.
     */
    public ExternalOperation(Operation operation,
                             NetworkParameters networkParameters,
                             LinkBuilder linkBuilder,
                             CurrencyDisplayMode currencyDisplayMode) {

        super(operation, networkParameters, linkBuilder, currencyDisplayMode);
    }

    @Override
    public CharSequence getFormattedTitle(Context context, boolean shortName) {
        if (isCyclical()) {
            return context.getString(R.string.operation_sent_to_yourself);

        } else if (isIncoming()) {
            return context.getString(R.string.external_incoming_operation);

        } else {
            return context.getString(R.string.external_outgoing_operation);
        }
    }

    @Override
    public String getPictureUri(Context context) {

        final int resId;
        if (isSwap() || isIncomingSwap()) {
            resId = R.drawable.lightning_badge;

        } else {
            resId = R.drawable.detail_bitcoin_logo;
        }

        return UiUtils.getResourceUri(context, resId).toString();
    }
}
