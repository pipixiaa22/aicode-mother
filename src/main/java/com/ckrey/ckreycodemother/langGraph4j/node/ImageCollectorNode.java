package com.ckrey.ckreycodemother.langGraph4j.node;

import com.ckrey.ckreycodemother.langGraph4j.ai.ImageCollectionService;
import com.ckrey.ckreycodemother.langGraph4j.state.WorkflowContext;
import com.ckrey.ckreycodemother.utils.SpringApplicationUtil;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.AsyncNodeAction;
import org.bsc.langgraph4j.prebuilt.MessagesState;

import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

@Slf4j
public class ImageCollectorNode {
    public static AsyncNodeAction<MessagesState<String>> create() {
        return node_async(state -> {
            WorkflowContext context = WorkflowContext.getContext(state);
            log.info("执行节点: 图片收集");

            // TODO: 实际执行图片收集逻辑
        //通过上下文获取springbean

            String originalPrompt = context.getOriginalPrompt();
            String imageListStr = "";

            try {
                ImageCollectionService imageCollectionService = SpringApplicationUtil.getBean(ImageCollectionService.class);
                imageListStr = imageCollectionService.collectImages(originalPrompt);

            }
            catch (Exception e){
                log.error("图片收集失败：{}",e.getMessage(),e);
            }
            context.setImageListStr(imageListStr);

            // 更新状态
            context.setCurrentStep("图片收集");
//            context.setImageList(imageList);
//            log.info("图片收集完成，共收集 {} 张图片", imageList.size());
            return WorkflowContext.saveContext(context);
        });
    }
}
