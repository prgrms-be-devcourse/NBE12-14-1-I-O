export default async function OrderDetailPage({
    params,
  }: {
    params: Promise<{ orderId: string }>;
  }) {
    const { orderId } = await params;
  
    return (
      <main className="mx-auto mt-8 max-w-6xl rounded-[40px] bg-white p-12">
        <h2 className="text-3xl font-bold">
          주문 상세
        </h2>
  
        <div className="mt-8">
          주문번호: {orderId}
        </div>
      </main>
    );
  }