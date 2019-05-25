/*
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.basics;

import com.google.common.collect.ImmutableMap;
import com.opengamma.strata.basics.date.HolidayCalendar;
import com.opengamma.strata.basics.date.HolidayCalendars;

import java.util.HashMap;
import java.util.Map;

/**
 * 为常用货币的HolidayCalendar提供标准参考数据。
 */
final class StandardReferenceData {

    /**
     * 标准参考数据
     */
    static final ImmutableReferenceData STANDARD;

    static {
        Map<ReferenceDataId<?>, Object> map = new HashMap<>();
        //获取modules\basics\src\main\resources\META-INF\com\opengamma\strata\config\base\HolidayCalendarDefaultData.ini
        //中的参考数据组装成{id=实例对象}的map集合
        for (HolidayCalendar cal : HolidayCalendars.extendedEnum().lookupAllNormalized().values()) {
            map.put(cal.getId(), cal);
        }
        STANDARD = ImmutableReferenceData.of(map);
    }

    /**
     * 最小参考数据。
     */
    static final ImmutableReferenceData MINIMAL;

    static {
        ImmutableMap.Builder<ReferenceDataId<?>, Object> builder = ImmutableMap.builder();
        builder.put(HolidayCalendars.NO_HOLIDAYS.getId(), HolidayCalendars.NO_HOLIDAYS);
        builder.put(HolidayCalendars.SAT_SUN.getId(), HolidayCalendars.SAT_SUN);
        builder.put(HolidayCalendars.FRI_SAT.getId(), HolidayCalendars.FRI_SAT);
        builder.put(HolidayCalendars.THU_FRI.getId(), HolidayCalendars.THU_FRI);
        MINIMAL = ImmutableReferenceData.of(builder.build());
    }

    private StandardReferenceData() {
    }

}
