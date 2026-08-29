'use client';

import { Order } from "@/types/Order";
import { formatDate } from "@/utils/FormatDate";
import Link from "next/link";
import { use, useEffect, useState } from "react";
import { useRouter } from "next/navigation";

export default function OrderDetailPage({
                                          params,
                                          searchParams,
                                        }: {
  params: Promise<{ orderId: string }>;
  searchParams: Promise<{ from?: string }>;
}) {
  const { orderId } = use(params);
  const { from } = use(searchParams);

  const closeHref = from === "checkout" ? "/" : "/orders";

  const router = useRouter();

  const [order, setOrder] = useState<Order | null>(null);

  const [isEditing, setIsEditing] = useState(false);

  const [address, setAddress] = useState("");
  const [postalCode, setPostalCode] = useState("");

  // 주문 상세 조회
  useEffect(() => {
    fetch(`http://localhost:8080/api/v1/orders/${orderId}`)
        .then((res) => {
          if (!res.ok) {
            throw new Error(
                `주문 상세 조회 실패: ${res.status}`
            );
          }

          return res.json();
        })
        .then((data) => {
          const orderData = data.data ?? null;

          setOrder(orderData);

          if (orderData) {
            setAddress(orderData.address);
            setPostalCode(orderData.postalCode);
          }
        })
        .catch((error) => {
          console.error(
              "주문 상세 조회 오류:",
              error
          );

          setOrder(null);
        });
  }, [orderId]);

  // 주문 취소
  const handleCancelOrder = async () => {
    if (!order) {
      return;
    }

    if (order.orderStatus === "CANCELED") {
      alert("이미 취소된 주문입니다.");
      return;
    }

    const confirmed = confirm(
        "정말 주문을 취소하시겠습니까?"
    );

    if (!confirmed) {
      return;
    }

    try {
      const response = await fetch(
          `http://localhost:8080/api/v1/orders/${orderId}`,
          {
            method: "DELETE",
          }
      );

      if (!response.ok) {
        throw new Error(
            `주문 취소 실패: ${response.status}`
        );
      }

      alert("주문이 취소되었습니다.");

      router.push("/orders");

    } catch (error) {
      console.error(
          "주문 취소 오류:",
          error
      );

      alert(
          "주문 취소 중 오류가 발생했습니다."
      );
    }
  };

  // 수정 모드 시작
  const handleEditStart = () => {
    if (!order) {
      return;
    }

    if (order.orderStatus === "CANCELED") {
      alert(
          "취소된 주문은 수정할 수 없습니다."
      );

      return;
    }

    setAddress(order.address);
    setPostalCode(order.postalCode);

    setIsEditing(true);
  };

  // 수정 취소
  const handleEditCancel = () => {
    if (!order) {
      return;
    }

    setAddress(order.address);
    setPostalCode(order.postalCode);

    setIsEditing(false);
  };

  // 주소 + 우편번호 수정
  const handleUpdateOrder = async () => {
    if (!order) {
      return;
    }

    if (order.orderStatus === "CANCELED") {
      alert(
          "취소된 주문은 수정할 수 없습니다."
      );

      return;
    }

    if (!address.trim()) {
      alert("주소를 입력해주세요.");
      return;
    }

    if (!postalCode.trim()) {
      alert("우편번호를 입력해주세요.");
      return;
    }

    // 백엔드가 orderItemId, quantity를 요구하므로
    // 첫 번째 주문상품의 기존 값을 그대로 사용
    const firstItem = order.orderItemResponses?.[0];

    if (!firstItem) {
      alert(
          "주문 상품 정보를 찾을 수 없습니다."
      );

      return;
    }

    const params = new URLSearchParams({
      orderItemId: String(firstItem.orderItemId),

      // 수량은 기존 값을 그대로 전달
      quantity: String(firstItem.quantity),

      address: address,
      postalCode: postalCode,
    });

    try {
      const response = await fetch(
          `http://localhost:8080/api/v1/orders/${orderId}?${params.toString()}`,
          {
            method: "PATCH",
          }
      );

      if (!response.ok) {
        throw new Error(
            `주문 수정 실패: ${response.status}`
        );
      }

      // 수정 성공 후 프론트 상태 갱신
      setOrder({
        ...order,
        address: address,
        postalCode: postalCode,
      });

      setIsEditing(false);

      alert("배송지가 수정되었습니다.");

    } catch (error) {
      console.error(
          "주문 수정 오류:",
          error
      );

      alert(
          "주문 수정 중 오류가 발생했습니다."
      );
    }
  };

  if (order === null) {
    return (
        <div className="m-4 flex justify-center text-2xl font-bold">
          데이터를 찾을 수 없습니다.
        </div>
    );
  }

  const isCanceled =
      order.orderStatus === "CANCELED";

  return (
      <main className="mx-auto mt-8 max-w-7xl rounded-[40px] bg-white p-12">

        <section className="flex min-h-[560px] flex-col rounded-xl border border-neutral-300 bg-[#fffaf0] p-10">

          {/* 주문 번호 */}
          <h2 className="text-3xl font-medium">
            주문 번호: {order.orderId}
          </h2>

          {/* 주문 정보 + Summary */}
          <div className="mt-8 flex gap-16">

            {/* 왼쪽 주문 정보 */}
            <section className="w-[420px] rounded-xl border border-neutral-300 bg-white p-6">

              <div className="space-y-5">

                {/* 주문번호 */}
                <div className="flex justify-between">
                <span className="font-bold">
                  주문번호
                </span>

                  <span>
                  {order.orderId}
                </span>
                </div>

                {/* 주문날짜 */}
                <div className="flex justify-between">
                <span className="font-bold">
                  주문날짜
                </span>

                  <span>
                  {formatDate(order.orderedAt)}
                </span>
                </div>

                {/* 주문상태 */}
                <div className="flex justify-between">
                <span className="font-bold">
                  주문상태
                </span>

                  <span>
                  {isCanceled
                      ? "주문 취소"
                      : "주문 완료"}
                </span>
                </div>

                {/* 배송상태 */}
                <div className="flex justify-between">
                <span className="font-bold">
                  배송상태
                </span>

                  <span>
                  {order.deliveryStatus === "ORDERED"
                      ? "배송 전"
                      : order.deliveryStatus === "SHIPPING"
                          ? "배송 중"
                          : order.deliveryStatus === "DELIVERED"
                              ? "배송 완료"
                              : order.deliveryStatus}
                </span>
                </div>

                {/* 총 가격 */}
                <div className="flex justify-between">
                <span className="font-bold">
                  총 가격
                </span>

                  <span>
                  {order.price.toLocaleString()}원
                </span>
                </div>

                {/* 주소 */}
                <div className="flex justify-between gap-6">
                <span className="shrink-0 font-bold">
                  주소
                </span>

                  {isEditing ? (
                      <input
                          type="text"
                          value={address}
                          onChange={(e) =>
                              setAddress(e.target.value)
                          }
                          className="
                      w-[250px]
                      rounded-md
                      border
                      border-neutral-300
                      px-3 py-2
                    "
                      />
                  ) : (
                      <span className="text-right">
                    {order.address}
                  </span>
                  )}
                </div>

                {/* 우편번호 */}
                <div className="flex justify-between gap-6">
                <span className="shrink-0 font-bold">
                  우편번호
                </span>

                  {isEditing ? (
                      <input
                          type="text"
                          value={postalCode}
                          onChange={(e) =>
                              setPostalCode(e.target.value)
                          }
                          className="
                      w-[250px]
                      rounded-md
                      border
                      border-neutral-300
                      px-3 py-2
                    "
                      />
                  ) : (
                      <span>
                    {order.postalCode}
                  </span>
                  )}
                </div>

              </div>
            </section>

            {/* 오른쪽 Summary */}
            <aside className="ml-auto w-[380px] self-start rounded-xl border border-neutral-300 bg-white p-7">

              <h2 className="text-4xl">
                Summary
              </h2>

              <hr className="my-5 border-neutral-400" />

              <div className="space-y-4">

                {(order.orderItemResponses ?? []).map(
                    (item) => (
                        <div
                            key={item.orderItemId}
                            className="flex justify-between gap-6"
                        >
                    <span>
                      {item.name} × {item.quantity}
                    </span>

                          <span className="shrink-0">
                      {(
                          item.price * item.quantity
                      ).toLocaleString()}
                            원
                    </span>
                        </div>
                    )
                )}

              </div>

              <hr className="my-6 border-neutral-400" />

              <div className="flex justify-between text-lg">

              <span>
                총 금액
              </span>

                <span>
                {order.price.toLocaleString()}원
              </span>

              </div>

            </aside>

          </div>

          {/* 취소된 주문 안내 */}
          {isCanceled && (
              <div className="mt-8 text-right font-bold text-red-500">
                취소된 주문은 수정하거나 다시 취소할 수 없습니다.
              </div>
          )}

          {/* 하단 버튼 */}
          <div className="mt-auto flex justify-end gap-4 pt-10">

            {isEditing ? (
                <>
                  {/* 수정 완료 */}
                  <button
                      onClick={handleUpdateOrder}
                      className="
                  rounded-md
                  bg-neutral-800
                  px-6 py-3
                  text-white
                "
                  >
                    수정 완료
                  </button>

                  {/* 수정 취소 */}
                  <button
                      onClick={handleEditCancel}
                      className="
                  rounded-md
                  bg-neutral-500
                  px-6 py-3
                  text-white
                "
                  >
                    수정 취소
                  </button>
                </>
            ) : (
                <>
                  {/* 주문 취소 */}
                  <button
                      onClick={handleCancelOrder}
                      disabled={isCanceled}
                      className="
                  rounded-md
                  bg-red-500
                  px-6 py-3
                  text-white
                  disabled:cursor-not-allowed
                  disabled:bg-neutral-400
                  disabled:opacity-50
                "
                  >
                    {isCanceled
                        ? "취소 완료"
                        : "주문 취소"}
                  </button>

                  {/* 주문 수정 */}
                  <button
                      onClick={handleEditStart}
                      disabled={isCanceled}
                      className="
                  rounded-md
                  bg-neutral-800
                  px-6 py-3
                  text-white
                  disabled:cursor-not-allowed
                  disabled:bg-neutral-400
                  disabled:opacity-50
                "
                  >
                    주문 수정
                  </button>
                </>
            )}

            {/* 닫기 */}
            <button
                onClick={() => router.back()}
                className="
              rounded-md
              bg-neutral-700
              px-6 py-3
              text-white
            "
            >
              닫기
            </button>

          </div>

        </section>

      </main>
  );
}