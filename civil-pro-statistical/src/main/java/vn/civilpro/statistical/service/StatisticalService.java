package vn.civilpro.statistical.service;

import vn.civil.grpc.statistical.GetDashboardResponse;
import vn.civil.grpc.statistical.GetPopulationTableResponse;
import vn.civilpro.statistical.entity.PopulationStatistic;

public interface StatisticalService {

    GetDashboardResponse getDashboard(String areaCode);

    GetPopulationTableResponse getPopulationTable(String parentAreaCode, int areaLevel, int page, int size);

    PopulationStatistic recordStatistic(PopulationStatistic statistic);
}
