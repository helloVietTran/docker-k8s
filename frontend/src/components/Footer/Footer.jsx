import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faCopyright } from '@fortawesome/free-regular-svg-icons';
import { faAngleUp } from '@fortawesome/free-solid-svg-icons';
import Container from '@/components/Layout/Container/Container';
import hashtag from '@/config/hastag';

function Footer() {
  const [isVisible, setIsVisible] = useState(false);

  useEffect(() => {
    const handleScroll = () => {
      if (window.scrollY > 400) {
        setIsVisible(true);
      } else {
        setIsVisible(false);
      }
    };
    window.addEventListener('scroll', handleScroll);
    return () => {
      window.removeEventListener('scroll', handleScroll);
    };
  }, []);

  const scrollToTop = () => {
    window.scrollTo({
      top: 0,
      behavior: 'smooth',
    });
  };

  return (
    <div className="bg-footer-background !pt-[15px] text-sm">
      {isVisible && (
        <span
          className="cursor-pointer w-10 h-10 flex items-center justify-center border border-[#bbb] 
          rounded-sm fixed right-[10px] bottom-[30px] z-[900] hover:border-[#aaa]"
          onClick={scrollToTop}
        >
          <FontAwesomeIcon icon={faAngleUp} className="text-[#ee2c3f]" />
        </span>
      )}
      <Container>
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-4 lg:gap-10">
          {/* Left column - 4/12 */}
          <div className="lg:col-span-4">
            <iframe
              title="fanpage"
              src="https://www.facebook.com/plugins/page.php?href=https%3A%2F%2Fwww.facebook.com%2Fprofile.php%3Fid%3D61556648275676&tabs=timeline%2C%20messages&width=340&height=230&small_header=true&adapt_container_width=true&hide_cover=false&show_facepile=true&appId"
              width="324"
              height="230"
              style={{ border: 'none', overflow: 'hidden' }}
              scrolling="no"
              frameBorder="0"
              allowFullScreen
              allow="autoplay; clipboard-write; encrypted-media; picture-in-picture; web-share"
            ></iframe>

            <p className="text-gray-300 text-sm flex items-center gap-1 !py-1">
              Copyright
              <FontAwesomeIcon icon={faCopyright} />
              2024 Viet Anh
            </p>
          </div>

          {/* Right column - 8/12 */}
          <div className="lg:col-span-8 text-gray-200">
            <h4 className="text-xl my-4">Từ khóa</h4>
            <ul className="flex flex-wrap gap-2">
              {hashtag.map((item, key) => (
                <li key={key} className="leading-8">
                  <Link
                    to="/"
                    className="!p-1 border border-gray-200 rounded-sm"
                  >
                    {item}
                  </Link>
                </li>
              ))}
            </ul>
            <hr className="my-[10px]" />
            <div className="flex justify-between text-gray-200">
              <Link to="/" className="hover:underline">
                Liên hệ bản quyền
              </Link>
              <Link to="/" className="hover:underline">
                Chính sách bảo mật
              </Link>
              <Link to="/" className="hover:underline">
                Liên hệ nội dung
              </Link>
            </div>
          </div>
        </div>
      </Container>
    </div>
  );
}

export default Footer;
