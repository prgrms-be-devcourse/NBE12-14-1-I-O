import Image from "next/image";
import { ChangeEvent, SubmitEvent, useRef, useState } from "react";

interface ProductAddModalProps {
    onClose: () => void;
}

export default function ProductAddModal({ onClose }: ProductAddModalProps) {
    const [name, setName] = useState('');
    const [price, setPrice] = useState('');
    const [stock, setStock] = useState('');
    // 실제 파일
    const [imageFile, setImageFile] = useState<File | null>(null);
    // 미리보기용 주소
    const [previewUrl, setPreviewUrl] = useState<string>('/baseThumbnail.png');

    const fileInputRef = useRef<HTMLInputElement>(null);

    const handleImageChange = (e: ChangeEvent<HTMLInputElement>) => {
        if (e.target.files && e.target.files[0]) {
            const file = e.target.files[0];
            setImageFile(file);
            setPreviewUrl(URL.createObjectURL(file));
        }
    }

    const handleSubmit = async (e: SubmitEvent) => {
        e.preventDefault();

        const formData = new FormData();

        const requestData = {
            name: name,
            price: Number(price),
            stock: Number(stock)
        };

        const jsonBlob = new Blob([JSON.stringify(requestData)], {
            type: 'application/json'
        });

        formData.append('request', jsonBlob);
        if (imageFile) {
            formData.append('image', imageFile);
        }

        try {
            const response = await fetch('http://localhost:8080/api/v1/admin/products', {
                method: 'POST',
                body: formData,
            });

            if (response.ok) {
                alert('상품이 등록되었습니다.');
                onClose();
            } else {
                const errorText = await response.text();
                alert(`상품 등록 실패: ${errorText}`);
            }
        } catch (error) {
            console.log('백엔드 서버와의 통신 에러', error);
            alert('백엔드 서버 통신 에러');
        }
    }

    return (
        <div className="fixed inset-0 flex justify-center items-center bg-black/30">
            <section className="flex flex-col rounded-xl border bg-gray-200 p-8 w-[35vw] h-[70vh]">
                <div>
                    <h1 className="text-center mb-8 text-3xl font-bold">상품 추가</h1>
                </div>
                <form onSubmit={handleSubmit} className="flex flex-col mx-16 gap-y-8">
                    <div className="flex flex-col gap-2">
                        <label htmlFor="name" className="font-bold text-xl">상품명</label>
                        <input onChange={(e) => setName(e.target.value)} type="text" name="name" id="name" className="p-2 rounded border bg-white" placeholder="상품명을 입력해주세요." />
                    </div>
                    <div className="flex flex-col gap-2">
                        <label htmlFor="price" className="font-bold text-xl">가격</label>
                        <input onChange={(e) => setPrice(e.target.value)} type="text" name="price" id="price" className="p-2 rounded border bg-white" placeholder="가격을 입력해주세요." />
                    </div>
                    <div className="flex flex-col gap-2">
                        <label htmlFor="stock" className="font-bold text-xl">재고</label>
                        <input onChange={(e) => setStock(e.target.value)} type="text" name="stock" id="stock" className="p-2 rounded border bg-white" placeholder="재고를 입력해주세요." />
                    </div>
                    <div className="flex items-end gap-2">
                        <input
                            type="file"
                            accept="image/*"
                            id="image"
                            ref={fileInputRef}
                            onChange={handleImageChange}
                            className="hidden"
                        />
                        {/* <img className="w-20 h-20"></img> */}
                        <Image src={previewUrl} alt="기본 상품 이미지" width={70} height={70}></Image>
                        <label htmlFor="image" className="border rounded w-48 p-1">
                            {imageFile ? imageFile.name : "선택된 파일 없음"}
                        </label>
                        <button onClick={() => fileInputRef.current?.click()}
                            type="button" className="rounded p-2 bg-gray-700 text-white text-sm hover:bg-black">이미지 선택</button>
                    </div>
                    <div className="flex gap-4 justify-center">
                        <button type="submit" className="w-24 rounded p-2 bg-gray-700 text-white text-m hover:bg-black">
                            상품 추가
                        </button>
                        <button onClick={onClose} type="button" className="w-24 rounded p-2 bg-gray-700 text-white text-m hover:bg-black">
                            취소
                        </button>
                    </div>
                </form>
            </section>
        </div>
    )
}