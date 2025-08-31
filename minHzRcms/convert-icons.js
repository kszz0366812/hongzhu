const fs = require('fs');
const path = require('path');
const zlib = require('zlib');

// 简单的SVG到PNG转换 - 使用zlib压缩
function createPNGFromSVG(svgPath, pngPath, color = '#999999') {
  try {
    const svgContent = fs.readFileSync(svgPath, 'utf8');
    
    // 创建一个简单的PNG文件（100x100像素，与SVG尺寸一致）
    const width = 100;
    const height = 100;
    const data = Buffer.alloc(width * height * 4); // RGBA
    
    // 设置透明背景
    for (let i = 0; i < data.length; i += 4) {
      data[i] = 0;     // R
      data[i + 1] = 0; // G
      data[i + 2] = 0; // B
      data[i + 3] = 0; // A (透明)
    }
    
    // 根据SVG内容绘制图标
    const iconName = path.basename(svgPath, '.svg');
    drawIcon(data, width, height, iconName, color);
    
    // 创建PNG文件
    const pngData = createPNG(data, width, height);
    
    fs.writeFileSync(pngPath, pngData);
    console.log(`Created ${pngPath}`);
  } catch (error) {
    console.error(`Error creating ${pngPath}:`, error.message);
  }
}

function createPNG(data, width, height) {
  // PNG signature
  const signature = Buffer.from([0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A]);
  
  // IHDR chunk
  const ihdrData = Buffer.alloc(13);
  ihdrData.writeUInt32BE(width, 0);   // width
  ihdrData.writeUInt32BE(height, 4);  // height
  ihdrData.writeUInt8(8, 8);          // bit depth
  ihdrData.writeUInt8(6, 9);          // color type (RGBA)
  ihdrData.writeUInt8(0, 10);         // compression
  ihdrData.writeUInt8(0, 11);         // filter
  ihdrData.writeUInt8(0, 12);         // interlace
  
  const ihdrChunk = createChunk('IHDR', ihdrData);
  
  // 准备图像数据（添加过滤器字节）
  const imageData = Buffer.alloc(height * (width * 4 + 1));
  for (let y = 0; y < height; y++) {
    const filterByte = 0; // 无过滤
    imageData[y * (width * 4 + 1)] = filterByte;
    
    for (let x = 0; x < width; x++) {
      const srcIndex = (y * width + x) * 4;
      const dstIndex = y * (width * 4 + 1) + 1 + x * 4;
      
      imageData[dstIndex] = data[srcIndex];     // R
      imageData[dstIndex + 1] = data[srcIndex + 1]; // G
      imageData[dstIndex + 2] = data[srcIndex + 2]; // B
      imageData[dstIndex + 3] = data[srcIndex + 3]; // A
    }
  }
  
  // 压缩图像数据
  const compressedData = zlib.deflateSync(imageData);
  const idatChunk = createChunk('IDAT', compressedData);
  
  // IEND chunk
  const iendChunk = createChunk('IEND', Buffer.alloc(0));
  
  return Buffer.concat([signature, ihdrChunk, idatChunk, iendChunk]);
}

function createChunk(type, data) {
  const length = Buffer.alloc(4);
  length.writeUInt32BE(data.length, 0);
  
  const typeBuffer = Buffer.from(type, 'ascii');
  const crc = Buffer.alloc(4);
  
  const chunkData = Buffer.concat([typeBuffer, data]);
  const crcValue = calculateCRC(chunkData);
  crc.writeUInt32BE(crcValue, 0);
  
  return Buffer.concat([length, chunkData, crc]);
}

function calculateCRC(data) {
  let crc = 0xFFFFFFFF;
  for (let i = 0; i < data.length; i++) {
    crc ^= data[i];
    for (let j = 0; j < 8; j++) {
      crc = (crc & 1) ? (0xEDB88320 ^ (crc >>> 1)) : (crc >>> 1);
    }
  }
  return (crc ^ 0xFFFFFFFF) >>> 0;
}

function drawIcon(data, width, height, iconName, color) {
  const [r, g, b] = hexToRgb(color);
  
  switch(iconName) {
    case 'default-avatar':
      drawDefaultAvatarIcon(data, width, height, [r, g, b, 255]);
      break;
  }
}

function drawDefaultAvatarIcon(data, width, height, color) {
  const centerX = 50;
  const centerY = 50;
  
  // 绘制背景圆形
  const bgRadius = 45;
  for (let y = centerY - bgRadius; y <= centerY + bgRadius; y++) {
    for (let x = centerX - bgRadius; x <= centerX + bgRadius; x++) {
      const distance = Math.sqrt((x - centerX) ** 2 + (y - centerY) ** 2);
      if (distance <= bgRadius) {
        setPixel(data, width, x, y, [232, 232, 232, 255]); // 浅灰色背景
      }
    }
  }
  
  // 绘制头部（圆形，比身体小）
  const headRadius = 12;
  const headCenterY = centerY - 10; // 头部位置保持不变
  
  for (let y = headCenterY - headRadius; y <= headCenterY + headRadius; y++) {
    for (let x = centerX - headRadius; x <= centerX + headRadius; x++) {
      const distance = Math.sqrt((x - centerX) ** 2 + (y - headCenterY) ** 2);
      if (distance <= headRadius) {
        setPixel(data, width, x, y, color);
      }
    }
  }
  
  // 绘制身体（半圆形，切面在下）
  const bodyRadius = 20;
  const bodyCenterY = centerY + 16; // 身体位置下移2像素（从14改为16）
  
  for (let y = bodyCenterY - bodyRadius; y <= bodyCenterY + bodyRadius; y++) {
    for (let x = centerX - bodyRadius; x <= centerX + bodyRadius; x++) {
      const distance = Math.sqrt((x - centerX) ** 2 + (y - bodyCenterY) ** 2);
      // 只绘制上半部分（半圆形，切面在下）
      if (distance <= bodyRadius && y <= bodyCenterY) {
        setPixel(data, width, x, y, color);
      }
    }
  }
}

function setPixel(data, width, x, y, color) {
  if (x >= 0 && x < width && y >= 0 && y < 100) {
    const index = (y * width + x) * 4;
    data[index] = color[0];     // R
    data[index + 1] = color[1]; // G
    data[index + 2] = color[2]; // B
    data[index + 3] = color[3]; // A
  }
}

function hexToRgb(hex) {
  const result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
  return result ? [
    parseInt(result[1], 16),
    parseInt(result[2], 16),
    parseInt(result[3], 16)
  ] : [0, 0, 0];
}

// 转换默认头像
const avatarSvgPath = path.join('utils', 'images', 'default-avatar.svg');
const avatarPngPath = path.join('utils', 'images', 'default-avatar.png');
createPNGFromSVG(avatarSvgPath, avatarPngPath, '#999999'); 