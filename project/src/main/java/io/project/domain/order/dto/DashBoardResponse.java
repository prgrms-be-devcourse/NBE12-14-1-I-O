package io.project.domain.order.dto;

import java.util.List;

import static io.project.domain.delivery.repository.DeliveryRepository.*;

public record DashBoardResponse(List<RevenueDashBoard> revenueDashBoards,
                                List<SoldTop3DashBoard> soldTop3DashBoards,
                                List<RevenueTop3DashBoard> revenueTop3DashBoards) {
}