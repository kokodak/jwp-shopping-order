package cart.cartitem.infrastructure.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.times;

import cart.cartitem.domain.CartItem;
import cart.cartitem.infrastructure.persistence.dao.CartItemDao;
import cart.cartitem.infrastructure.persistence.mapper.CartItemEntityMapper;
import cart.member.domain.Member;
import cart.product.domain.Product;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("JdbcCartItemRepository 은(는)")
class JdbcCartItemRepositoryTest {

    @InjectMocks
    private JdbcCartItemRepository jdbcCartItemRepository;

    @Mock
    private CartItemDao cartItemDao;

    @Test
    void 장바구니_상품을_저장한다() {
        // given
        Product product = new Product("말랑", 1000, "image");
        Member member = new Member(null, "email", "1234");
        CartItem cartItem = new CartItem(product, member);
        given(cartItemDao.insert(any()))
                .willReturn(1L);

        // when
        Long id = jdbcCartItemRepository.save(cartItem);

        // then
        assertThat(id).isEqualTo(1L);
        then(cartItemDao).should(times(1))
                .insert(any());
    }

    @Test
    void 장바구니_상품을_수정한다() {
        // given
        Product product = new Product("말랑", 1000, "image");
        Member member = new Member(null, "email", "1234");
        CartItem cartItem = new CartItem(product, member);

        // when
        jdbcCartItemRepository.update(cartItem);

        // then
        then(cartItemDao).should(times(1))
                .updateQuantity(any());
    }

    @Test
    void 장바구니_상품을_ID_로_조회한다() {
        // given
        Product product = new Product("말랑", 1000, "image");
        Member member = new Member(null, "email", "1234");
        CartItem cartItem = new CartItem(product, member);
        given(cartItemDao.findById(cartItem.getId()))
                .willReturn(Optional.of(CartItemEntityMapper.toEntity(cartItem)));

        // when
        CartItem found = jdbcCartItemRepository.findById(cartItem.getId());

        // then
        assertThat(cartItem).usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(found);
    }

    @Test
    void 특정_회원의_장바구니_상품을_조회한다() {
        // given
        Product product = new Product("말랑", 1000, "image");
        Member member = new Member(1L, "email", "1234");
        CartItem cartItem = new CartItem(product, member);
        given(cartItemDao.findByMemberId(member.getId()))
                .willReturn(List.of(CartItemEntityMapper.toEntity(cartItem)));

        // when
        List<CartItem> cartItems = jdbcCartItemRepository.findByMemberId(member.getId());

        // then
        assertThat(cartItems).hasSize(1);
    }

    @Test
    void ID_리스트로_모든_장바구니_상품을_조회한다() {
        // given
        Product product = new Product("코코닥", 1000, "image");
        Member member = new Member(null, "email", "1234");
        CartItem cartItem1 = new CartItem(1L, 10, product, member);
        CartItem cartItem2 = new CartItem(2L, 15, product, member);
        given(cartItemDao.findAllByIds(List.of(cartItem1.getId(), cartItem2.getId())))
                .willReturn(List.of(CartItemEntityMapper.toEntity(cartItem1), CartItemEntityMapper.toEntity(cartItem2)));

        // when
        List<CartItem> actual = jdbcCartItemRepository.findAllByIds(List.of(1L, 2L));

        // then
        List<CartItem> expected = List.of(cartItem1, cartItem2);
        assertThat(actual).usingRecursiveComparison()
                .isEqualTo(expected);
    }
}
