package com.dwl.rep.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.dwl.rep.common.Constants;
import com.dwl.rep.common.SqlEexecuter;
import com.dwl.rep.common.Strings;
import com.dwl.rep.dao.ChartInfoMapper;
import com.dwl.rep.dao.DataInfoMapper;
import com.dwl.rep.pojo.ChartInfo;

@Service("chartService")
public class ChartService {
	
	
	@Resource
	private ChartInfoMapper chartInfoMapper;
	
	@Resource
	private DataInfoMapper dataInfoMapper;
	
	public List<ChartInfo> getChartList(){
		return chartInfoMapper.selectList();
	}
	
	public List<ChartInfo> getChartCacheList(){
		return chartInfoMapper.selectCacheList();
	}
	
	public ChartInfo getChartById(String id) {
		return chartInfoMapper.selectByPrimaryKey(id);
	}
	
	public int addChartInfo(ChartInfo chartInfo){
		chartInfo.setUpdateTime(new Date());
		return chartInfoMapper.insertSelective(chartInfo);
	}
	
	public int deleteChartInfo(String id){
		return chartInfoMapper.deleteByPrimaryKey(id);
	}
	
	public int updateChartInfo(ChartInfo chartInfo){
		chartInfo.setUpdateTime(new Date());
		return chartInfoMapper.updateByPrimaryKeySelective(chartInfo);
	}
	
	
	//页面展示测试
	public String getChartResultById(String id) {
		ChartInfo info = chartInfoMapper.selectByPrimaryKey(id);
		if("1".equals(info.getIsCache())&&!Strings.isEmpty(info.getResult()))
			return info.getResult();
		List<Map<String, Object>> result = SqlEexecuter.getInstance()
				.getResult(dataInfoMapper.selectWithDbByPrimaryKey(info.getDataId()));
		Map<String, Object> dataInfo = new HashMap<>();
		dataInfo.put("name", info.getChartName());
		dataInfo.put("type", info.getChartType());
		if("pie".equals(info.getChartType())){
			List<Map<String, Object>> resultData = new ArrayList<>();
			result.forEach(item->{
				Map<String, Object> data = new HashMap<>();
				item.keySet().forEach(name->{
					if(Constants.VALUE.equals(name)){
						data.put(Constants.VALUE, item.get(name));
					}else{
						data.put("name", item.get(name));
					}
				});
				resultData.add(data);
			});
			dataInfo.put("data", resultData);
		}else{
			List<Object> xList = new ArrayList<>();
			List<Object> yList = new ArrayList<>();
			result.forEach(item->{
				item.keySet().forEach(name->{
					if(Constants.VALUE.equals(name)){
						yList.add(item.get(name));//y轴
					}else{
						xList.add(item.get(name));//x轴
					}
				});
			});
			dataInfo.put("xlist", xList);
			dataInfo.put("ylist", yList);
		}
		return JSON.toJSONString(dataInfo);
	}

}
