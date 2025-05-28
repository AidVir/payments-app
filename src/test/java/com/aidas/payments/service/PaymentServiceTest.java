package com.aidas.payments.service;

import com.aidas.payments.dto.PaymentRequest;
import com.aidas.payments.dto.PaymentResponse;
import com.aidas.payments.entity.Payment;
import com.aidas.payments.enums.Currency;
import com.aidas.payments.enums.PaymentType;
import com.aidas.payments.exception.BusinessException;
import com.aidas.payments.exception.NotFoundException;
import com.aidas.payments.handler.PaymentHandler;
import com.aidas.payments.handler.PaymentHandlerFactory;
import com.aidas.payments.mapper.PaymentMapper;
import com.aidas.payments.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

    private PaymentRepository repository;
    private PaymentHandlerFactory handlerFactory;
    private AsyncTaskService asyncService;
    private PaymentService service;

    private final PaymentHandler handler = mock(PaymentHandler.class);

    @BeforeEach
    void setup() {
        repository = mock(PaymentRepository.class);
        handlerFactory = mock(PaymentHandlerFactory.class);
        asyncService = mock(AsyncTaskService.class);
        PaymentMapper mapper = mock(PaymentMapper.class);

        service = new PaymentServiceImpl(repository, handlerFactory, asyncService, mapper);
    }

    @Test
    void shouldCreatePaymentAndReturnId() {
        PaymentRequest request = PaymentRequest.builder()
                .type(PaymentType.TYPE1)
                .currency(Currency.EUR)
                .amount(BigDecimal.valueOf(500))
                .debtorIban("LT123")
                .creditorIban("LT456")
                .details("test")
                .build();

        Payment entity = Payment.builder()
                .id(1L)
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .type(request.getType())
                .debtorIban(request.getDebtorIban())
                .creditorIban(request.getCreditorIban())
                .createdAt(LocalDateTime.now())
                .build();

        when(handlerFactory.getHandler(PaymentType.TYPE1)).thenReturn(handler);
        when(repository.save(any())).thenReturn(entity);

        Long id = service.createPayment(request, "1.2.3.4");

        assertThat(id).isEqualTo(1L);
        verify(handler).validate(request);
        verify(handler).addTypeSpecificProperties(any(), eq(request));
        verify(asyncService).resolveCountry(any(), eq(1L));
    }

    @Test
    void shouldCancelPaymentAndCalculateFee() {
        LocalDateTime now = LocalDateTime.now();
        Payment payment = Payment.builder()
                .id(1L)
                .type(PaymentType.TYPE2)
                .currency(Currency.USD)
                .amount(BigDecimal.valueOf(200))
                .debtorIban("X")
                .creditorIban("Y")
                .createdAt(now.minusHours(3))
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(payment));
        when(handlerFactory.getHandler(PaymentType.TYPE2)).thenReturn(handler);
        when(handler.calculateCancellationFee(payment)).thenReturn(BigDecimal.valueOf(0.30));

        service.cancelPayment(1L);

        verify(repository).save(argThat(saved ->
                saved.isCancelled() &&
                        saved.getCancellationFee().compareTo(BigDecimal.valueOf(0.30)) == 0));
    }

    @Test
    void shouldThrowWhenCancellingAlreadyCancelledPayment() {
        Payment payment = Payment.builder()
                .id(1L)
                .type(PaymentType.TYPE3)
                .cancelled(true)
                .createdAt(LocalDateTime.now())
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(payment));

        assertThatThrownBy(() -> service.cancelPayment(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("cannot be cancelled");
    }

    @Test
    void shouldThrowWhenCancellingNotSameDay() {
        Payment payment = Payment.builder()
                .id(1L)
                .type(PaymentType.TYPE1)
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(payment));

        assertThatThrownBy(() -> service.cancelPayment(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("cannot be cancelled");
    }

    @Test
    void shouldFilterByAmountRange() {
        Payment p1 = Payment.builder().amount(BigDecimal.valueOf(100)).build();
        Payment p2 = Payment.builder().amount(BigDecimal.valueOf(200)).build();

        when(repository.findByAmountRange(BigDecimal.valueOf(150), BigDecimal.valueOf(300)))
                .thenReturn(List.of(p2));

        List<Payment> result = service.getPaymentsByAmountRange(
                BigDecimal.valueOf(150), BigDecimal.valueOf(300));

        assertThat(result)
                .extracting(Payment::getAmount)
                .containsExactly(BigDecimal.valueOf(200));
    }

    @Test
    void shouldReturnPaymentById() {
        Payment payment = Payment.builder()
                .id(10L)
                .cancelled(false)
                .type(PaymentType.TYPE1)
                .currency(Currency.EUR)
                .createdAt(LocalDateTime.now().minusHours(2))
                .build();

        when(repository.findById(10L)).thenReturn(Optional.of(payment));

        PaymentResponse response = service.getPaymentById(10L);

        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getCancellationFee()).isNull();
    }


    @Test
    void shouldThrowWhenPaymentNotFound() {
        when(repository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getPaymentById(404L))
                .isInstanceOf(NotFoundException.class);
    }
}

