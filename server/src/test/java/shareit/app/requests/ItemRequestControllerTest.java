package shareit.app.requests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import shareit.app.requests.dto.ItemRequestDto;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ItemRequestService service;

    @Autowired
    private ObjectMapper objectMapper;

    private final String url = "/requests";

    @Test
    public void addRequest() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "desc", null, List.of());
        when(service.add(anyLong(), any())).thenReturn(itemRequestDto);

        mockMvc.perform(postRequest(itemRequestDto, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())));
    }

    @Test
    public void addRequestWithoutHeader() throws Exception {
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "desc", null, List.of());
        when(service.add(anyLong(), any())).thenReturn(itemRequestDto);

        mockMvc.perform(MockMvcRequestBuilders.post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(this.objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getAllByRequester() throws Exception {
        when(service.getAllByRequester(anyLong())).thenReturn(List.of(new ItemRequestDto(), new ItemRequestDto()));

        mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void getAllByRequesterWithoutHeader() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(url))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getAllExceptRequesterWithoutHeader() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(url + "/all"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getById() throws Exception {
        when(service.getById(any(), any()))
                .thenReturn(new ItemRequestDto());

        mockMvc.perform(MockMvcRequestBuilders.get(url + "/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()));
    }

    @Test
    public void getByIdWithoutHeader() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(url + "/1"))
                .andExpect(status().isBadRequest());
    }

    private MockHttpServletRequestBuilder postRequest(ItemRequestDto itemRequestDto,
                                                      Long requesterId) throws JsonProcessingException {
        return MockMvcRequestBuilders.post(url)
                .header("X-Sharer-User-Id", requesterId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(itemRequestDto));
    }
}
