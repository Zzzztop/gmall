package com.ztop.gmall.gmall.manager.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import com.ztop.gmall.bean.*;
import com.ztop.gmall.config.RedisUtil;
import com.ztop.gmall.gmall.manager.constant.ManageConst;
import com.ztop.gmall.gmall.manager.mapper.*;
import com.ztop.service.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ManagerServiceImpl implements ManagerService {

    @Autowired
    private BaseCatalog1Mapper baseCatalog1Mapper;

    @Autowired
    private BaseCatalog2Mapper baseCatalog2Mapper;

    @Autowired
    private BaseCatalog3Mapper baseCatalog3Mapper;

    @Autowired
    private BaseAttrInfoMapper baseAttrInfoMapper;

    @Autowired
    private BaseAttrValueMapper baseAttrValueMapper;

    @Autowired
    private BaseSaleAttrMapper baseSaleAttrMapper;

    @Autowired
    private SpuInfoMapper spuInfoMapper;

    @Autowired
    private SpuImageMapper spuImageMapper;

    @Autowired
    private SpuSaleAttrMapper spuSaleAttrMapper;

    @Autowired
    private SpuSaleAttrValueMapper spuSaleAttrValueMapper;

    @Autowired
    private SkuInfoMapper skuInfoMapper;

    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Autowired
    private SkuAttrValueMapper skuAttrValueMapper;

    @Autowired
    private SkuImageMapper skuImageMapper;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public List<BaseCatalog1> getcatalog1() {
        List<BaseCatalog1> catalog1List = baseCatalog1Mapper.selectAll();
        return catalog1List;
    }

    @Override
    public List<BaseCatalog2> getcatalog2(String catalog1Id) {
        BaseCatalog2 baseCatalog2 = new BaseCatalog2();
        baseCatalog2.setCatalog1Id(catalog1Id);
        List<BaseCatalog2> catalog2List = baseCatalog2Mapper.select(baseCatalog2);
        return catalog2List;
    }

    @Override
    public List<BaseCatalog3> getcatalog3(String catalog2Id) {
        BaseCatalog3 baseCatalog3 = new BaseCatalog3();
        baseCatalog3.setCatalog2Id(catalog2Id);
        List<BaseCatalog3> Catalog3List = baseCatalog3Mapper.select(baseCatalog3);
        return Catalog3List;
    }

    @Override
    public List<BaseAttrInfo> getAttrList(String catalog3Id) {
        List<BaseAttrInfo> InfoList = baseAttrInfoMapper.getBaseAttrInfoListByCatalog3Id(catalog3Id);
        return InfoList;
    }

    @Override
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        //进行数据校验 如果有主键就进行更新 没有就插入数据
        if(baseAttrInfo.getId().length()==0 || baseAttrInfo.getId()==null){
            baseAttrInfo.setId(null);
            baseAttrInfoMapper.insertSelective(baseAttrInfo);
        }else{
            baseAttrInfoMapper.updateByPrimaryKey(baseAttrInfo);
        }
        //清空value
        BaseAttrValue baseAttrValue = new BaseAttrValue();
        baseAttrInfo.setCatalog3Id(baseAttrInfo.getId());
        baseAttrValueMapper.delete(baseAttrValue);
        //效验list是否为空
        if(baseAttrInfo.getAttrValueList()!=null && baseAttrInfo.getAttrValueList().size()>0){
            for (BaseAttrValue attrValue : baseAttrInfo.getAttrValueList()) {
                //防止主键被赋空字符串
                if(attrValue.getId().length()==0){
                    attrValue.setId(null);
                }
                attrValue.setAttrId(baseAttrInfo.getId());
                baseAttrValueMapper.insertSelective(attrValue);
            }
        }
    }

    @Override
    public BaseAttrInfo getAttrInfo(String attrId) {
        //根基三级分类id查询出所有value
        BaseAttrValue baseAttrValue = new BaseAttrValue();
        baseAttrValue.setAttrId(attrId);
        List<BaseAttrValue> attrValues = baseAttrValueMapper.select(baseAttrValue);
        //将查出的list放入info中传回去
        BaseAttrInfo BaseAttrInfo = new BaseAttrInfo();
        BaseAttrInfo.setAttrValueList(attrValues);
        return BaseAttrInfo;
    }

    @Override
    public void delAttrInfo(BaseAttrInfo baseAttrInfo) {
        if(baseAttrInfo.getId()!=null) {
            //先将info下value删除
            BaseAttrValue baseAttrValue = new BaseAttrValue();
            baseAttrValue.setAttrId(baseAttrInfo.getId());
            Example Example = new Example(BaseAttrValue.class);
            Example.createCriteria().andEqualTo(baseAttrValue);
            baseAttrValueMapper.deleteByExample(Example);

            //删除info
            baseAttrInfoMapper.deleteByPrimaryKey(baseAttrInfo);
        }
    }

    /**
     * 查询所有spuinfo
     * @param spuInfo
     * @return
     */
    @Override
    public List<SpuInfo> getSpuList(SpuInfo spuInfo) {
       return spuInfoMapper.select(spuInfo);
    }

    /**
     * 查询所有字典销售属性值
     * @return
     */
    @Override
    public List<BaseSaleAttr> getBaseSaleAttrList() {
        List<BaseSaleAttr> selectAll = baseSaleAttrMapper.selectAll();
        return selectAll;
    }

    /**
     * 保存spuInfo spuSaleAttr spuSaleAttrValue spuImage
     * @param spuInfo
     */
    @Override
    public void saveSpuInfo(SpuInfo spuInfo) {
        System.out.println("😀");
        if(spuInfo.getId()==null || spuInfo.getId().length()==0){
            spuInfo.setId(null);
            spuInfoMapper.insertSelective(spuInfo);
        }else{
            spuInfoMapper.updateByPrimaryKey(spuInfo);
        }
        //保存img信息前先清空 修改操作
        Example SpuImageExample = new Example(SpuImage.class);
        SpuImageExample.createCriteria().andEqualTo("spuId",spuInfo.getId());
        spuImageMapper.deleteByExample(SpuImageExample);

        List<SpuImage> spuImageList = spuInfo.getSpuImageList();
        if(spuImageList!=null){
            for (SpuImage spuImage : spuImageList) {
                if(spuImage.getId()!=null && spuImage.getId().length()==0){
                        spuImage.setId(null);
                }
                spuImage.setSpuId(spuInfo.getId());
                spuImageMapper.insertSelective(spuImage);
            }
        }
        //保存 销售信息前先清空 目的修改插入
        Example spuSaleAttrExample = new Example(SpuSaleAttr.class);
        spuSaleAttrExample.createCriteria().andEqualTo("spuId",spuInfo.getId());
        spuSaleAttrMapper.deleteByExample(spuSaleAttrExample);

        //保存 销售值信息前先删除 目的修改插入
        Example spuSaleAttrValueExample = new Example(SpuSaleAttrValue.class);
        spuSaleAttrValueExample.createCriteria().andEqualTo("spuId",spuInfo.getId());
        spuSaleAttrValueMapper.deleteByExample(spuSaleAttrValueExample);
        //获取前台传的销售参数集合
        List<SpuSaleAttr> spuSaleAttrList = spuInfo.getSpuSaleAttrList();
        if(spuSaleAttrList!=null){
            for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
                if(spuSaleAttr.getId()!=null && spuSaleAttr.getId().length()==0) {
                    spuSaleAttr.setId(null);
                }
                spuSaleAttr.setSpuId(spuInfo.getId());
                spuSaleAttrMapper.insertSelective(spuSaleAttr);
                //获取销售属性值集合
                List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttr.getSpuSaleAttrValueList();
                if(spuSaleAttrValueList!=null){
                    for (SpuSaleAttrValue spuSaleAttrValue : spuSaleAttrValueList) {
                        if(spuSaleAttrValue.getId()!=null && spuSaleAttrValue.getId().length()==0){
                            spuSaleAttrValue.setId(null);
                        }
                        spuSaleAttrValue.setSpuId(spuInfo.getId());
                        spuSaleAttrValueMapper.insertSelective(spuSaleAttrValue);
                    }
                }
            }
        }
    }

    /**
     * 根据spuId查询SaleAttr
     * @param spuId
     */
    @Override
    public List<SpuSaleAttr> getSpuSaleAttrList(String spuId) {
        if(spuId==null){
            throw new RuntimeException("spuSaleAttrList(spuId)为空！");
        }
       List<SpuSaleAttr> spuSaleAttrs = spuSaleAttrMapper.selectSpuSaleAttrList(spuId);
        return spuSaleAttrs;
    }

    /**
     * 获取spuInage集合
     * @param spuId
     * @return
     */
    @Override
    public List<SpuImage> getspuImageList(String spuId) {
        if(spuId==null){
            throw new  RuntimeException("getspuImageList(spuId)为空！");
        }
            Example example = new Example(SpuImage.class);
            example.createCriteria().andEqualTo("spuId",spuId);
            List<SpuImage> spuImages = spuImageMapper.selectByExample(example);
        return  spuImages;
    }

    /**
     * 删除spuInfo
     * @param spuInfo
     */
    @Override
    public void delSpuInfo(SpuInfo spuInfo) {
        //null 值判断
        if(spuInfo.getId()==null || spuInfo.getId().length()<=0){
            throw new RuntimeException("spuInfo为空！");
        }
        //调用方法根据当前Id获取image
        List<SpuImage> spuImageList = getspuImageList(spuInfo.getId());
        if(spuImageList==null || spuImageList.size()<=0){
            throw new RuntimeException("spuImageList !!!!!");
        }
        for (SpuImage spuImage : spuImageList) {
            spuImageMapper.delete(spuImage);
        }

        List<SpuSaleAttr> spuSaleAttrList =getSpuSaleAttrList(spuInfo.getId());
        if(spuSaleAttrList.size()<=0 || spuSaleAttrList==null){
            throw new RuntimeException("spuSaleAttrList!!!!!");
        }
        for (SpuSaleAttr spuSaleAttr : spuSaleAttrList) {
            Example example = new Example(SpuSaleAttrValue.class);
            example.createCriteria().andEqualTo(spuInfo.getId());
            List<SpuSaleAttrValue> spuSaleAttrValueList = spuSaleAttrValueMapper.selectByExample(example);
            if(spuSaleAttrValueList==null || spuSaleAttrValueList.size()<=0){
                throw new RuntimeException("spuSaleAttrValueList!!! ");
            }
            for (SpuSaleAttrValue spuSaleAttrValue : spuSaleAttrValueList) {
                spuSaleAttrValueMapper.delete(spuSaleAttrValue);
            }
            spuSaleAttrMapper.delete(spuSaleAttr);
        }
        spuInfoMapper.delete(spuInfo);
    }


    @Override
    public List<SpuSaleAttr> selectSpuSaleAttrList(String spuId) {
        return spuSaleAttrMapper.selectSpuSaleAttrList(spuId);
    }

    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {
       if(skuInfo.getId()==null || skuInfo.getId().length()==0){
           skuInfo.setId(null);
           skuInfoMapper.insertSelective(skuInfo);
       }else{
           skuInfoMapper.updateByPrimaryKeySelective(skuInfo);
       }

       SkuImage skuImage = new SkuImage();
       skuImage.setSkuId(skuInfo.getId());
       skuImageMapper.delete(skuImage);

        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        if(skuImageList!=null && skuImageList.size()>0){
            for (SkuImage image : skuImageList) {
                if(image.getId()!=null && image.getId().length()==0){
                    image.setId(null);
                }
                image.setSkuId(skuInfo.getId());
                skuImageMapper.insert(image);
            }
        }

        SkuAttrValue skuAttrValue1  =  new SkuAttrValue();
        skuAttrValue1.setSkuId(skuInfo.getId());
        skuAttrValueMapper.delete(skuAttrValue1);

        List<SkuAttrValue> skuAttrValueList = skuInfo.getSkuAttrValueList();
        if(skuAttrValueList!=null && skuAttrValueList.size()>0){
            for (SkuAttrValue skuAttrValue : skuAttrValueList) {
                if(skuAttrValue.getId()!=null && skuAttrValue.getId().length()==0){
                    skuAttrValue.setId(null);
                }
                skuAttrValue.setSkuId(skuInfo.getId());
                skuAttrValueMapper.insert(skuAttrValue);
            }
        }
            SkuSaleAttrValue skuSaleAttrValue1 = new SkuSaleAttrValue();
            skuSaleAttrValue1.setSkuId(skuInfo.getId());
            skuSaleAttrValueMapper.delete(skuSaleAttrValue1);

        List<SkuSaleAttrValue> skuSaleAttrValueList = skuInfo.getSkuSaleAttrValueList();
        if(skuSaleAttrValueList!=null && skuSaleAttrValueList.size()>0){
            for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
                if(skuSaleAttrValue.getId()!=null && skuSaleAttrValue.getId().length()==0){
                    skuSaleAttrValue.setId(null);
                }
                skuSaleAttrValue.setSkuId(skuInfo.getId());
                skuSaleAttrValueMapper.insert(skuSaleAttrValue);
            }
        }
    }

    @Override
    public List<SkuInfo> skuInfoListBySpu(String spuId) {
            SkuInfo skuInfo = new SkuInfo();
            skuInfo.setSpuId(spuId);
        return skuInfoMapper.select(skuInfo);
    }

    /**
     * 使用redis缓存获取skuInfo
     * @param skuId
     * @return
     */
    @Override
    public SkuInfo getSkuInfo(String skuId) {
        SkuInfo skuInfo = null ;
        try{
        //获取redis连接
        Jedis jedis = redisUtil.getJedis();
        //拼接Reids key
        String skuInfoKey = ManageConst.SKUKEY_PREFIX+skuId+ManageConst.SKUKEY_SUFFIX;
                //从redis中获取key下的值
                String skuInfoJson = jedis.get(skuInfoKey);
                //判断key如果不存在 没有命中缓存 防止缓存击穿
                if(skuInfoJson==null || skuInfoJson.length()==0){
                    System.out.println("没有命中缓存！");
                    //拼接一个锁key
                    String skuLockKey = ManageConst.SKUKEY_PREFIX+skuId+ManageConst.SKULOCK_SUFFIX;
                    //redis中设置生成一个key
                    String lockKey = jedis.set(skuLockKey, "OK", "NX", "PX", ManageConst.SKULOCK_EXPIRE_PX);
                    //设置成功返回Ok说明redis中没有值 进入if获取mysql数据
                    if("OK".equals(lockKey)){
                        // 从数据库中取得数据
                        skuInfo = getSkuInfoDB(skuId);
                        // 将对象转换成字符串
                        String skuRedisStr  = JSON.toJSONString(skuInfo);
                        // 将是数据放入缓存
                        jedis.setex(skuInfoKey,ManageConst.SKUKEY_TIMEOUT,skuRedisStr);
                        jedis.close();
                    }else{
                        //自旋
                        System.out.println("等待！");
                        Thread.sleep(1000);

                        return getSkuInfo(skuId);
                    }
                }else{
                    System.out.println("命中缓存！！");
                //将Json转换成对象
                skuInfo = JSON.parseObject(skuInfoJson, SkuInfo.class);
                jedis.close();
                return skuInfo;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return getSkuInfoDB(skuId);
    }



    public SkuInfo getSkuInfoDB(String skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectByPrimaryKey(skuId);
        SkuImage skuImage = new SkuImage();
        skuImage.setSkuId(skuInfo.getId());
        List<SkuImage> SkuImages = skuImageMapper.select(skuImage);
        //根据SkuIndo.Id查询出skuImage集合保存进SkuInfo.SkuImageList中
        skuInfo.setSkuImageList(SkuImages);
        return skuInfo;
    }


    @Override
    public List<SpuSaleAttr> selectSpuSaleAttrListCheckBySku(SkuInfo skuInfo) {
    List<SpuSaleAttr> spuSaleAttrList =
            spuSaleAttrMapper.selectSpuSaleAttrListCheckBySku(Long.parseLong(skuInfo.getId()), Long.parseLong(skuInfo.getSpuId()));
        return spuSaleAttrList;
    }

    /**
     * 根据spuId获取skuSaleAttrValueList
     * @param spuId
     * @return
     */
    @Override
    public List<SkuSaleAttrValue> getSkuSaleAttrValueListBySpu(String spuId) {
        List<SkuSaleAttrValue> skuSaleAttrValues = skuSaleAttrValueMapper.selectSkuSaleAttrValueListBySpu(spuId);
        return skuSaleAttrValues;
    }

    /**
     * 根据skuId删除skuInfo
     * @param skuId
     */
    @Override
    public void delSkuInfo(String skuId) {
       //非空判断
        if (skuId!=null && skuId.length()>0) {
            //删除销售属性值
            SkuSaleAttrValue skuSaleAttrValue = new SkuSaleAttrValue();
            skuSaleAttrValue.setSkuId(skuId);
            skuSaleAttrValueMapper.delete(skuSaleAttrValue);
            //删除sku属性值
            SkuAttrValue skuAttrValue = new SkuAttrValue();
            skuAttrValue.setSkuId(skuId);
            skuAttrValueMapper.delete(skuAttrValue);

            //删除sku图片路径
            SkuImage skuImage = new SkuImage();
            skuImage.setSkuId(skuId);
            skuImageMapper.delete(skuImage);

            //删除sku
            skuInfoMapper.deleteByPrimaryKey(skuId);
        }else{
            throw new RuntimeException("删除skuInfo传入Id为空");
        }

    }
}