type ProductFormProps = {
    title: string;
    submitText: string;
  };
  
  export default function ProductForm({
    title,
    submitText,
  }: ProductFormProps) {
    return (
      <section className="w-[460px] rounded-lg border bg-neutral-100 p-8">
        <h2 className="text-center text-2xl font-bold">
          {title}
        </h2>
  
        <div className="mt-8">
          <label className="font-bold">
            상품명
          </label>
  
          <input
            className="mt-2 w-full rounded border bg-white p-3"
            placeholder="상품명"
          />
        </div>
  
        <div className="mt-5">
          <label className="font-bold">
            가격
          </label>
  
          <input
            className="mt-2 w-full rounded border bg-white p-3"
            placeholder="10,000"
          />
        </div>
  
        <div className="mt-5">
          <label className="font-bold">
            재고
          </label>
  
          <input
            className="mt-2 w-full rounded border bg-white p-3"
            placeholder="10"
          />
        </div>
  
        <div className="mt-5">
          <label className="font-bold">
            이미지
          </label>
  
          <div className="mt-2 flex items-center gap-3">
            <div className="h-14 w-14 border bg-white" />
  
            <input
              className="flex-1 rounded border bg-white p-2"
              placeholder="경로"
            />
  
            <button className="rounded bg-neutral-800 px-3 py-2 text-white">
              이미지 업로드
            </button>
          </div>
        </div>
  
        <div className="mt-8 flex justify-center gap-4">
          <button className="rounded bg-neutral-800 px-5 py-2 text-white">
            {submitText}
          </button>
  
          <button className="rounded bg-neutral-600 px-5 py-2 text-white">
            취소
          </button>
        </div>
      </section>
    );
  }