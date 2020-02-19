// Owl Carousel
$('#customer-testi').owlCarousel({
  loop:false,
  nav: false,
  dots: true,
  autoplay:false,
  autoplayTimeout:3000,
  autoplayHoverPause:true,
  responsive:{
      0:{
          items:1
      },
      600:{
          items:2
      },
      1000:{
          items:3
      }
  }
});

$('#owl-fade').owlCarousel({
  loop:false,
  nav: false,
  dots: true,
  autoplay:false,
  autoplayTimeout:3000,
  autoplayHoverPause:true,
  animateOut: 'fadeOut',
  items: 1,
})