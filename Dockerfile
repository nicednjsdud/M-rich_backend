FROM ubuntu:22.04

# Tesseract 설치
RUN apt update && apt install -y tesseract-ocr libtesseract-dev && \
    apt install -y wget && \
    wget https://github.com/tesseract-ocr/tessdata_best/raw/main/kor.traineddata -O /usr/share/tesseract-ocr/4.00/tessdata/kor.traineddata && \
    wget https://github.com/tesseract-ocr/tessdata_best/raw/main/eng.traineddata -O /usr/share/tesseract-ocr/4.00/tessdata/eng.traineddata

# 환경 변수 설정
ENV TESSDATA_PREFIX=/usr/local/share/tessdata

WORKDIR /app
COPY . /app
CMD ["java", "-jar", "your-app.jar"]
