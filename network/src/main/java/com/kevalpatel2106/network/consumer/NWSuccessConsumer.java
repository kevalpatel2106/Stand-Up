package com.kevalpatel2106.network.consumer;

import com.kevalpatel2106.network.BaseData;
import com.kevalpatel2106.network.Response;

import io.reactivex.functions.Consumer;

/**
 * Created by Keval on 12/11/17.
 * This class is custom success consumer to take the received {@link Response} from retrofit and
 * pass the {@link BaseData} to the parent class.
 *
 * @author <a href="https://github.com/kevalpatel2106">kevalpatel2106</a>
 */
@Deprecated()
public abstract class NWSuccessConsumer<T extends BaseData> implements Consumer<Response<T>> {
    /**
     * Consume the given value.
     *
     * @param tResponse the value
     *
     * @throws Exception on error
     */
    @Override
    public void accept(final Response<T> tResponse) throws Exception {
        onSuccess(tResponse.getData());
    }

    /**
     * Success api response.
     *
     * @param data {@link BaseData}
     */
    public abstract void onSuccess(T data);
}
