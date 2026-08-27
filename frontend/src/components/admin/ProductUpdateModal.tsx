'use client';
import { Product } from "@/types/product";
import Image from "next/image";
import { ChangeEvent, SubmitEvent, useRef, useState } from "react";

interface ProductUpdateModalProps {
    product: Product;
    onClose: () => void;
}

export default function ProductUpdateModal({ product, onClose }: ProductUpdateModalProps) {
    const [name, setName] = useState(product.name);
    const [price, setPrice] = useState(String(product.price));
    const [stock, setStock] = useState(String(product.stock));
    
    // 실제 파일
    const [imageFile, setImageFile] = useState<File | null>(null);
    // 미리보기용 주소
    const [previewUrl, setPreviewUrl] = useState<string>(
        product.imageFileUrl ? `http://localhost:8080/${product.imageFileUrl}` : '/baseThumbnail.png'
    );

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
            stock: Number(stock),
            fileName: imageFile ? imageFile.name : product.imageFileUrl
        };

        const jsonBlob = new Blob([JSON.stringify(requestData)], {
            type: 'application/json'
        });

        formData.append('request', jsonBlob);
        if (imageFile) {
            formData.append('image', imageFile);
        }

        try {
            const response = await fetch(`http://localhost:8080/admin/products/${product.id}`, {
                method: 'PATCH',
                body: formData,
            });

            if (response.ok) {
                alert('상품이 수정되었습니다.');
                onClose();
            } else {
                const errorText = await response.text();
                alert(`상품 수정 실패: ${errorText}`);
            }
        } catch (error) {
            console.log('백엔드 서버와의 통신 에러', error);
            alert('백엔드 서버 통신 에러');
        }
    }

    return (
        <div className="fixed inset-0 flex justify-center items-center bg-black/30 z-50">
            <section className="flex flex-col rounded-xl border bg-gray-200 p-8 w-[35vw] h-[70vh]">
                <div>
                    <h1 className="text-center mb-8 text-3xl font-bold">상품 수정</h1>
                </div>
                <form onSubmit={handleSubmit} className="flex flex-col mx-16 gap-y-8">
                    <div className="flex flex-col gap-2">
                        <label htmlFor="name" className="font-bold text-xl">상품명</label>
                        <input 
                            value={name}
                            onChange={(e) => setName(e.target.value)}
                            type="text" name="name" id="name" 
                            className="p-2 rounded border bg-white" 
                            placeholder="상품명을 입력해주세요." 
                        />
                    </div>
                    <div className="flex flex-col gap-2">
                        <label htmlFor="price" className="font-bold text-xl">가격</label>
                        <input 
                            value={price}
                            onChange={(e) => setPrice(e.target.value)} 
                            type="text" name="price" id="price" 
                            className="p-2 rounded border bg-white" 
                            placeholder="가격을 입력해주세요." 
                        />
                    </div>
                    <div className="flex flex-col gap-2">
                        <label htmlFor="stock" className="font-bold text-xl">재고</label>
                        <input 
                            value={stock}
                            onChange={(e) => setStock(e.target.value)} 
                            type="text" name="stock" id="stock" 
                            className="p-2 rounded border bg-white" 
                            placeholder="재고를 입력해주세요." 
                        />
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
                        <Image src={previewUrl} alt="상품 이미지" width={70} height={70} className="object-cover rounded"></Image>
                        <label htmlFor="image" className="border rounded w-48 p-1 truncate block bg-white">
                            {imageFile ? imageFile.name : (product.imageFileUrl || "기본 이미지 상태")}
                        </label>
                        <button onClick={() => fileInputRef.current?.click()}
                            type="button" className="rounded p-2 bg-gray-700 text-white text-sm hover:bg-black">이미지 변경</button>
                    </div>
                    <div className="flex gap-4 justify-center">
                        <button type="submit" className="w-24 rounded p-2 bg-gray-700 text-white text-m hover:bg-black">
                            수정 완료
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
