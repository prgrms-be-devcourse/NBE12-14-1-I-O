package io.project.domain.order.dto;

import java.util.List;

public record OrderListGroupResponse(
        List<OrderListResponse> orderedList, //배송준비 중 리스트
        List<OrderListResponse> shippingList, //배송중 리스트
        List<OrderListResponse> deliveredList, //배송완료 리스트
        List<OrderListResponse> cancelledList, //주문취소 리스트
        int orderedTotalPrice,
        int shippingTotalPrice
) {}
