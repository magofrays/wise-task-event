package ru.leti.wise.task.event.service;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import ru.leti.wise.task.event.service.operations.GetCachedStatistic;

import static ru.leti.wise.task.event.Statistic.*;
import static ru.leti.wise.task.event.StatisticsServiceGrpc.*;


@GrpcService
@RequiredArgsConstructor
public class StatisticsServiceGrpc extends StatisticsServiceImplBase {
    private final GetCachedStatistic getCachedStatistic;

    @Override
    public void getStatistic(StatisticRequest request, StreamObserver<StatisticResponse> responseObserver){
        responseObserver.onNext(getCachedStatistic.getCachedStatistic(request));
        responseObserver.onCompleted();
    }

}
