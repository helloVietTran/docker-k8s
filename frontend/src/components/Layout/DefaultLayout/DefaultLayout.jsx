import PropTypes from 'prop-types';
import { useSelector } from 'react-redux';

function DefaultLayout({ children }) {
  const darkTheme = useSelector((state) => state.theme.darkTheme);

  return (
    <div
      className={`pt-10 ${darkTheme ? 'bg-dark-main-background' : 'bg-main-background'}`}
    >
      {children}
    </div>
  );
}

DefaultLayout.propTypes = {
  children: PropTypes.node.isRequired,
};

export default DefaultLayout;
