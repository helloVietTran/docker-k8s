import { Link } from 'react-router-dom';
import { options } from '@/config/filter';

function GenreMenu() {
  const quarterLength = options.length / 4;

  return (
    <div className="absolute top-full left-0 bg-white w-[600px] border border-gray-300 shadow-md z-[1000] py-1 pb-4">
      <div className="grid grid-cols-2 md:grid-cols-4 gap-y-2">
        {Array(4)
          .fill(0)
          .map((_, i) => (
            <ul key={i}>
              {options
                .slice(i * quarterLength, (i + 1) * quarterLength)
                .map((option) => (
                  <li key={option.name}>
                    <Link
                      to={option.path}
                      className={`block px-4 text-sm leading-8 text-gray-800 hover:text-purple-500 hover:bg-white ${
                        option.active ? 'text-red-600 font-bold' : ''
                      }`}
                    >
                      {option.name}
                    </Link>
                  </li>
                ))}
            </ul>
          ))}
      </div>
    </div>
  );
}

export default GenreMenu;
