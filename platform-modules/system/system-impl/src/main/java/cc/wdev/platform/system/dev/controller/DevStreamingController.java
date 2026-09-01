package cc.wdev.platform.system.dev.controller;

import cc.wdev.platform.commons.annotations.Anonymous;
import cc.wdev.platform.commons.annotations.OperationLog;
import cc.wdev.platform.commons.web.servlet.controller.AbstractController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static cc.wdev.platform.system.commons.constants.SystemMappingConstants.API_V1_PREFIX;

/**
 * @author elvea
 */
@RestController
@Tag(name = "DevStreamingController", description = "Streamable HTTP Controller")
public class DevStreamingController extends AbstractController {

    private final ExecutorService executor = Executors.newCachedThreadPool();

    @Anonymous
    @OperationLog("SseEmitter")
    @Operation(summary = "SseEmitter")
    @ApiResponse(description = "SseEmitter")
    @ResponseBody
    @GetMapping(API_V1_PREFIX + "/dev/sse")
    public SseEmitter sse() {
        SseEmitter emitter = new SseEmitter();
        executor.execute(() -> {
            try {
                emitter.send(sse());
                emitter.send("/sse" + " @ " + LocalDateTime.now());
                emitter.complete();
            } catch (Exception ex) {
                emitter.completeWithError(ex);
            }
        });
        return emitter;
    }

    @Anonymous
    @OperationLog("ResponseBodyEmitter")
    @Operation(summary = "ResponseBodyEmitter")
    @ApiResponse(description = "ResponseBodyEmitter")
    @ResponseBody
    @GetMapping(API_V1_PREFIX + "/dev/rbe")
    public ResponseEntity<ResponseBodyEmitter> rbe() {
        ResponseBodyEmitter emitter = new ResponseBodyEmitter();
        executor.execute(() -> {
            try {
                emitter.send("/rbe" + " @ " + LocalDateTime.now(), MediaType.TEXT_PLAIN);
                emitter.complete();
            } catch (Exception ex) {
                emitter.completeWithError(ex);
            }
        });
        return new ResponseEntity<>(emitter, HttpStatus.OK);
    }

    @Anonymous
    @OperationLog("StreamingResponseBody")
    @Operation(summary = "StreamingResponseBody")
    @ApiResponse(description = "StreamingResponseBody")
    @ResponseBody
    @GetMapping(API_V1_PREFIX + "/dev/srb")
    public ResponseEntity<StreamingResponseBody> srb() {
        StreamingResponseBody stream = out -> {
            String msg = "/srb" + " @ " + LocalDateTime.now();
            out.write(msg.getBytes());
        };
        return new ResponseEntity<>(stream, HttpStatus.OK);
    }

}
